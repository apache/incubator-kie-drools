/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.definition.type.FactField;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.MapCore;
import org.drools.rule.TypeDeclaration;
import org.drools.util.HierarchyEncoder;
import org.drools.util.HierarchyEncoderImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitRegistry implements Externalizable {


    private Map<String, ClassDefinition> traits;
    private Map<String, ClassDefinition> traitables;

    private int codeSize = 0;

    private Map<String, Long> masks;

    private HierarchyEncoder<String> hierarchy = new HierarchyEncoderImpl<String>();


    public TraitRegistry() {

        init();
    }

    private void init() {
        TypeDeclaration thingType = new TypeDeclaration( Thing.class.getName() );
        thingType.setKind( TypeDeclaration.Kind.TRAIT );
        thingType.setTypeClass( Thing.class );
        ClassDefinition def = new ClassDefinition();
        def.setClassName( thingType.getTypeClass().getName() );
        def.setDefinedClass( Thing.class );
        addTrait( def );

        ClassDefinition individualDef = new ClassDefinition();
        individualDef.setClassName( Entity.class.getName() );
        individualDef.setDefinedClass( Entity.class );
        individualDef.setInterfaces( new String[] { Serializable.class.getName(), TraitableBean.class.getName() } );
        individualDef.setTraitable( true );
        addTraitable( individualDef );

        ClassDefinition mapcoreDef = new ClassDefinition();
        mapcoreDef.setClassName( MapCore.class.getName() );
        mapcoreDef.setDefinedClass( MapCore.class );
        mapcoreDef.setInterfaces( new String[] { Serializable.class.getName(), TraitableBean.class.getName() } );
        mapcoreDef.setTraitable( true );
        addTraitable( mapcoreDef );
    }

    public void merge( TraitRegistry other ) {
        if ( traits == null ) {
            traits = new HashMap<String, ClassDefinition>();
        }
        if ( other.traits != null ) {
            this.traits.putAll( other.traits );
        }

        if ( traitables == null ) {
            traitables = new HashMap<String, ClassDefinition>();
        }
        if ( other.traitables != null ) {
            this.traitables.putAll( other.traitables );
        }

        if ( masks == null ) {
            masks = new HashMap<String, Long>();
        }
        if ( other.masks != null ) {
            this.masks.putAll( other.masks );
        }

        if ( hierarchy == null || hierarchy.size() <= 1 ) {
            hierarchy = other.hierarchy;
        } else {
            if ( other.traits != null ) {
                for ( String traitName : other.traits.keySet() ) {
                    ClassDefinition trait = other.traits.get( traitName );
                    List<String> parentTraits = new ArrayList<String>( );
                    for ( String candidateIntf : trait.getInterfaces() ) {
                        if ( hierarchy.getCode( candidateIntf ) != null ) {
                            parentTraits.add( candidateIntf );
                        }
                    }
                    hierarchy.encode( trait.getName(), parentTraits );
                }
            }
        }
    }

    public Map<String, ClassDefinition> getTraits() {
        return traits;
    }

    protected ClassDefinition getTrait( String key ) {
        if ( key.endsWith(  TraitFactory.SUFFIX ) ) {
            key = key.replace(  TraitFactory.SUFFIX , "" );
        }
        ClassDefinition traitDef = traits != null ? traits.get( key ) : null;
        if ( traitDef == null ) {

        }
        return traitDef;
    }

    public Map<String, ClassDefinition> getTraitables() {
        return traitables;
    }

    protected ClassDefinition getTraitable( String key ) {
        return traitables != null ? traitables.get( key ) : null;
    }


    public void addTrait( ClassDefinition trait ) {
        addTrait( trait.getClassName(), trait );
    }


    public void addTrait( String className, ClassDefinition trait ) {
        if ( traits == null ) {
            traits = new HashMap<String, ClassDefinition>();
        }
        this.traits.put( className, trait );
        hierarchy.encode( className, getTraitInterfaces( trait ) );
    }

    private Collection<String> getTraitInterfaces( ClassDefinition trait ) {
        List<String> intfs = new ArrayList<String>();
        for ( String s : trait.getInterfaces() ) {
            if ( traits.containsKey( s ) ) {
                intfs.add( s );
            }
        }
        return intfs;
    }

    public void addTraitable( ClassDefinition traitable ) {
        if ( traitables == null ) {
            traitables = new HashMap<String, ClassDefinition>();
        }
        this.traitables.put( traitable.getClassName(), traitable );
    }



    public static boolean isSoftField( FieldDefinition field, int index, long mask ) {
        return (mask & (1 << index)) == 0;
    }

    public long getFieldMask( String trait, String traitable ) {
        if ( masks == null ) {
            masks = new HashMap<String, Long>();
        }
        String key = trait + traitable;
        Long mask = masks.get( key );

        if ( mask == null ) {
            mask = bind( trait, traitable );
            masks.put( key, mask );
        }

        return mask;
    }

    private Long bind( String trait, String traitable ) throws UnsupportedOperationException {
        ClassDefinition traitDef = getTrait( trait );
        if ( traitDef == null ) {
            throw new UnsupportedOperationException( " Unable to apply trait " + trait + " to class " + traitable + " : not a trait " );
        }
        ClassDefinition traitableDef = getTraitable( traitable );
        if ( traitableDef == null ) {
            throw new UnsupportedOperationException( " Unable to apply trait " + trait + " to class " + traitable + " : not a traitable " );
        }

        int j = 0;
        long bitmask = 0;
        for ( FactField field : traitDef.getFields() ) {
            String alias = ((FieldDefinition) field).resolveAlias();

            FieldDefinition concreteField = traitableDef.getFieldByAlias( alias );

            if ( concreteField != null ) {
                if ( ! traitableDef.isFullTraiting() && ! concreteField.getType().isAssignableFrom( field.getType() ) ) {
                    throw new UnsupportedOperationException( " Unable to apply trait " + trait + " to class " + traitable + " :" +
                                                             " trait field " + field.getName() + ":" + ( (FieldDefinition) field ).getTypeName() + " is incompatible with" +
                                                             " concrete hard field " + concreteField.getName() + ":" + concreteField.getTypeName() + ". Consider enabling logical traiting" +
                                                             " mode using @Traitable( logical = true )" );
                }

                bitmask |= 1 << j;
            }
            j++;
        }

        return bitmask;
    }


    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject( traits );
        objectOutput.writeObject( traitables );
        objectOutput.writeObject( masks );
        objectOutput.writeObject( hierarchy );
        objectOutput.writeInt( codeSize );
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        traits = (Map<String, ClassDefinition>) objectInput.readObject();
        traitables = (Map<String, ClassDefinition>) objectInput.readObject();
        masks = (Map<String, Long>) objectInput.readObject();
        hierarchy = (HierarchyEncoderImpl) objectInput.readObject();
        codeSize = objectInput.readInt();
        init();
    }


    public HierarchyEncoder getHierarchy() {
        return hierarchy;
    }



}
