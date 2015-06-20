package org.drools.core.factmodel.traits;

import org.drools.core.WorkingMemory;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.KnowledgeHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

public class TraitField implements Serializable, Externalizable {

    private Object value;
    private boolean isExplicitlySet = false;
    private boolean explicitSetEnabled = true;

    // the type of the field, as in the core class.
    private PriorityQueue<TypeWrapper> rangeTypes;

    // type restrictions added by traits donned by the core object
    private TypeHierarchy<Object,TraitFieldDefaultValue> defaultValuesByTraits;
    private Object defaultValueByClass;
    private short position;

    public TraitField() {
    }

    public TraitField( TypeWrapper klass, Object value, Object defaultValue, short pos ) {
        this.rangeTypes = new PriorityQueue<TypeWrapper>( 1, TypeComparator.instance() );
        this.rangeTypes.offer( klass );

        this.defaultValueByClass = defaultValue;
        // TODO NEED BETTER SET/UNSET MANAGEMENT
        isExplicitlySet = value != null && ! value.equals( zero( klass.getKlass() ) );

        this.value = value;
        this.position = pos;
    }

    public Object set( Object value, TypeWrapper klass, WorkingMemory wm ) {
        Class<?> base = rangeTypes.peek().getKlass();

        if ( klass.getKlass() == null ) {
            // we are deserializing
            this.value = value;
            return value;
        }

        if ( value != null ) {
            if ( base.isAssignableFrom( klass.getKlass() ) ) {
                this.value = value;
                isExplicitlySet |= explicitSetEnabled;
            } else {
                if ( value instanceof TraitProxy ) {
                    set( ( (TraitProxy) value ).getObject(), klass, wm );
                } else if ( value instanceof TraitableBean ) {
                    set( ( (TraitableBean) value ).getTrait( klass.getName() ), klass, wm );
                }
            }
        }

        if ( value == null ) {
            this.value = getDefault();
            isExplicitlySet = false;
        }

        if ( ! explicitSetEnabled ) {
            enableExplicitSet();
        }

        ensureTypes( base, wm );

        return this.value;
    }

    private void ensureTypes( Class<?> base, WorkingMemory wm ) {
        if ( this.value != null ) {
            Iterator<TypeWrapper> typeIterator = rangeTypes.iterator();
            while ( typeIterator.hasNext() ) {
                Class<?> range = typeIterator.next().getKlass();
                if ( range != base ) {
                    boolean hasType = ensureType( this.value, range, wm );
                    if ( ! hasType ) {
                        manageInconsistentValue( range );
                    }
                }
            }
        }
    }


    public Object get() {
        return value;
    }

    public Object get( TypeWrapper klass ) {
        if ( value == null ) {
            return null;
        } else if ( klass.getKlass() != null && klass.getKlass().isInstance( value ) ) {
            return value;
        } else if ( value instanceof TraitableBean ) {
            return ( (TraitableBean) value ).getTrait( klass.getName() );
        } else if ( value instanceof TraitProxy ) {
            return ( (TraitProxy) value ).getObject();
        } else if ( klass.getKlass() == null ) {
            // possible during deserialization, when
            return value;
        }
        return null;
    }

    private boolean ensureType( Object value, Class<?> range, WorkingMemory wm ) {

        if ( range.isInstance( value ) ) {
            // check value directly
            return true;
        }
        // otherwise, we need a traitable bean to continue searching
        TraitableBean obj = null;
        if ( value instanceof TraitableBean ) {
            obj = (TraitableBean) value;
        } else if ( value instanceof TraitProxy ) {
            obj = (TraitableBean) ( (TraitProxy) value ).getObject();
        }
        if ( obj == null ) {
            return false;
        }

        if ( range.isInstance( obj ) ) {
            // the core object has the type
            return true;
        } else if ( obj.hasTrait( range.getName() ) ) {
            // the range is a trait that the object has
            return true;
        } else {
            // the range is a trait that can be donned automatically
            Trait trait = range.getAnnotation( Trait.class );
            if ( trait != null && trait.logical() ) {
                // set can't be undone, so any cascaded don is not logical
                Object newTrait = don( obj, range, false, wm );
                return newTrait != null;
            }
        }

        // type is missing, we're inconsistent
        return false;
    }


    public Object don( TraitType trait, Object defaultValue, TypeWrapper wrapper, boolean logical, WorkingMemory wm ) {
        disableExplicitSet();
        Class<?> klass = wrapper.getKlass();

        if ( defaultValue != null ) {
            if ( defaultValuesByTraits == null ) {
                defaultValuesByTraits = new DefaultValueHierarchy();
            }
            defaultValuesByTraits.addMember( defaultValue, trait._getTypeCode() );
            if ( defaultValuesByTraits.getBottomCode() == null ) {
                defaultValuesByTraits.setBottomCode( (BitSet) trait._getTypeCode().clone() );
            } else {
                defaultValuesByTraits.getBottomCode().or( trait._getTypeCode() );
            }
        }

        try {
            rangeTypes.offer( wrapper );

            if ( value != null ) {
                if ( klass.isInstance( value ) ) {
                    // ok
                } else if ( value instanceof TraitableBean ) {
                    // The property's object is traitable, the trait applied to the property's subject
                    // may trying to apply a cascaded trait
                    return donTraitable( (TraitableBean) value, klass, logical, wm );

                } else if ( value instanceof TraitProxy ) {
                    // dual case : the field's original type is a trait,
                    // we need to consider the current proxy's core and see if it is compatible
                    TraitableBean core = (TraitableBean) ( (TraitProxy) value ).getObject();
                    return donTraitable( core, klass, logical, wm );

                } else {
                    boolean isFullyTraitable = inspectForTraitability( value, wm );
                    if ( isFullyTraitable ) {
                        TraitProxy proxy = (TraitProxy) don( value, klass, logical, wm );
                        value = proxy.getObject();
                        return proxy;
                    } else {
                        manageInconsistentValue( klass );
                    }
                }
            }
        } catch ( IllegalStateException ise ) {
            throw new UnsupportedOperationException( "Unable to apply field traiting, incompatible type.", ise );
        }

        if ( ! isExplicitlySet ) {
            return getDefault();
        }

        return this.value;
    }

    private boolean inspectForTraitability( Object value, WorkingMemory wm ) {
        InternalKnowledgePackage pack = wm.getKnowledgeBase().getPackage( value.getClass().getPackage().getName() );
        if ( pack != null ) {
            TypeDeclaration decl = pack.getTypeDeclaration( value.getClass() );
            if ( decl != null ) {
                return decl.getTypeClassDef().isFullTraiting();
            }
        }
        Traitable tbl =  value.getClass().getAnnotation( Traitable.class );
        return tbl != null && tbl.logical();
    }


    private Object donTraitable( TraitableBean obj, Class<?> klass, boolean logical, WorkingMemory wm ) {
        if ( klass.isInstance( obj ) ) {
            return obj;
        } else if ( obj.hasTraits() && obj.hasTrait( klass.getName() ) ) {
            // klass may be a trait, and that trait is already available: remark to ensure logical status is set correctly
            return don( obj, klass, logical, wm );
        } else {
            if ( klass.isInterface() ) {
                Trait ta = klass.getAnnotation( Trait.class );
                if ( ta != null && ta.logical() ) {
                    // Only apply trait if in logical (non constraint) mode
                    return don( obj, klass, logical, wm );
                } else {
                    return manageInconsistentValue( klass );
                }
            } else {
                return manageInconsistentValue( klass );
            }
        }
    }

    private Object don( Object obj, Class<?> klass, boolean logical, WorkingMemory wm ) {
        KnowledgeHelper knowledgeHelper = ((DefaultAgenda) wm.getAgenda()).getKnowledgeHelper();
        return knowledgeHelper.don( obj, klass, logical );
    }

    private Object manageInconsistentValue( Class klass ) {
        //value = null;
        //isExplicitlySet = false;
        throw new UnsupportedOperationException( "Unable to apply field traiting, incompatible type " + klass + " for current value " + this.value );
    }


    public Object shed( TraitType trait, TypeWrapper rangeWrapper, TypeWrapper asWrapper, WorkingMemory workingMemory ) {
        if ( this.defaultValuesByTraits != null ) {
            this.defaultValuesByTraits.removeMember( trait._getTypeCode() );
        }

        this.rangeTypes.remove( rangeWrapper );

        if ( ! isExplicitlySet ) {
            this.value = getDefault();
        }

        /*
        if ( this.value != null ) {
            ensureTypes( asWrapper.getKlass(), workingMemory );
        }
        */

        Class<?> klass = asWrapper.getKlass();
        if ( this.value == null || klass.isInstance( this.value ) ) {
            return this.value;
        } else if ( this.value instanceof TraitableBean ) {
            return ((TraitableBean) this.value).getTrait( klass.getName() );
        } else if ( this.value instanceof TraitProxy ) {
            return ((TraitProxy) this.value).getObject().getTrait( klass.getName() );
        } else {
            throw new IllegalStateException( "Logical field shed : illegal value for a field : " + this.value + ", class expected " + klass.getName() );
        }

    }


    public Object getDefault() {
        if ( defaultValueByClass != null ) {
            return defaultValueByClass;
        }
        if ( defaultValuesByTraits != null && ! defaultValuesByTraits.isEmpty() ) {
            Collection<Object> lowerBorder = defaultValuesByTraits.upperBorder( defaultValuesByTraits.getBottomCode() );
            if ( lowerBorder.size() > 1 ) {
                return null;
            } else {
                return lowerBorder.iterator().next();
            }
        }
        return null;
    }

    public void disableExplicitSet() {
        this.explicitSetEnabled = false;
    }
    public void enableExplicitSet() {
        this.explicitSetEnabled = true;
    }

    public Set<Class<?>> getRangeTypes() {
        Set<Class<?>> set = new HashSet<Class<?>>( rangeTypes.size() );
        for ( TypeWrapper type : rangeTypes ) {
            set.add( type.getKlass() );
        }
        return Collections.unmodifiableSet( set );
    }

    @Override
    public String toString() {
        return "TF{ " + value + " }";
    }


    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( value );
        out.writeBoolean( isExplicitlySet );
        out.writeBoolean( explicitSetEnabled );

        out.writeObject( rangeTypes );

        out.writeObject( defaultValuesByTraits );
        out.writeObject( defaultValueByClass );

        out.writeShort( position );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        value = in.readObject();
        isExplicitlySet = in.readBoolean();
        explicitSetEnabled = in.readBoolean();

        rangeTypes = (PriorityQueue<TypeWrapper>) in.readObject();

        defaultValuesByTraits = (TypeHierarchy<Object,TraitFieldDefaultValue>) in.readObject();
        defaultValueByClass = in.readObject();

        position = in.readShort();
    }

    public short getPosition() {
        return position;
    }


    private static class TypeComparator implements Comparator<TypeWrapper>, Serializable {

        private static TypeComparator singleton = new TypeComparator();

        public static TypeComparator instance() {
            return singleton;
        }

        public int compare( TypeWrapper t1, TypeWrapper t2 ) {
            Class o1 = t1.getKlass();
            Class o2 = t2.getKlass();
            if ( o1 == o2 ) return 0;
            if ( o2.isAssignableFrom( o1 ) ) { return 1; }
            if ( o1.isAssignableFrom( o2 ) ) { return -1; }

            boolean trait1 = o1.isInterface() && o1.getAnnotation( Trait.class ) != null;
            boolean trait2 = o2.isInterface() && o2.getAnnotation( Trait.class ) != null;
            if ( trait1 || trait2 ) { return 1; }

            throw new IllegalStateException( "Types " + o1.getName() + " and " + o2 + " are incompatible" );
        }

    }


    private static Object zero( Class<?> klass ) {
        if ( Integer.class == klass ) { return Integer.valueOf( 0 ); }
        if ( Boolean.class == klass ) { return Boolean.valueOf( false ); }
        if ( Float.class == klass ) { return Float.valueOf( 0.0f ); }
        if ( Long.class == klass ) { return Long.valueOf( 0L ); }
        if ( Double.class == klass ) { return Double.valueOf( 0.0 ); }
        if ( Short.class == klass ) { return Short.valueOf( (short) 0 ); }
        if ( Byte.class == klass ) { return Byte.valueOf( (byte) 0 ); }
        if ( Character.class == klass ) { return Character.valueOf( (char) 0 ); }
        return null;
    }

    public static class DefaultValueHierarchy extends TypeHierarchy<Object,TraitFieldDefaultValue> implements Externalizable {

        public DefaultValueHierarchy() {

        }

        protected TraitFieldDefaultValue wrap( Object val, BitSet key ) {
            return new TraitFieldDefaultValue( val, key );
        }
    }

}
