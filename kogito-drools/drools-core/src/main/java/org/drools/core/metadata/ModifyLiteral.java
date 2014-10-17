package org.drools.core.metadata;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.bitmask.BitMask;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.setPropertyOnMask;

public abstract class ModifyLiteral<T extends Metadatable> extends AbstractWMTask<T> implements Modify<T>, Serializable {
    private T target;
    private ModifyTaskLiteral<T,?,?> task;
    private BitMask modificationMask;
    private URI key;
    private Object[] with;
    private BitMask[] extraMasks;

    protected abstract MetaClass<T> getMetaClassInfo();

    public ModifyLiteral( T target, With[] with ) {
        this.target = target;
        switch ( with.length ) {
            case 0 : this.with = null;
                this.extraMasks = null;
                return;
            case 1 : this.with = with[ 0 ].getArgs();
                break;
            default :
                mergeWiths( with );
        }
        this.extraMasks = new BitMask[ this.with.length ];
    }

    protected void mergeWiths( With[] with ) {
        int n = 0;
        for ( int j = 0; j < with.length; j++ ) {
            n += with[ j ].getArgs().length;
        }
        this.with = new Object[ n ];
        n = 0;
        for ( int j = 0; j < with.length; j++ ) {
            System.arraycopy( with[ j ].getArgs(), 0, this.with, n, with[ j ].getArgs().length );
            n += with[ j ].getArgs().length;
        }
    }


    public T getTarget() {
        return target;
    }

    void setTarget( T target ) {
        this.target = target;
    }

    public ModifyTask getSetterChain() {
        return task;
    }

    @Override
    public Object getTargetId() {
        return MetadataContainer.getIdentifier( target );
    }

    @Override
    public Object[] getAdditionalUpdates() {
        return with;
    }

    @Override
    public BitMask getAdditionalUpdatesModificationMask( int j ) {
        return extraMasks[ j ];
    }

    @Override
    public KIND kind() {
        return KIND.MODIFY;
    }

    public T call( T object ) {
        setTarget( object );
        return call();
    }

    public T call() {
        computeModificationMasks( null );
        task.call( target );
        return target;
    }

    public T call( InternalKnowledgeBase knowledgeBase ) {
        computeModificationMasks( knowledgeBase );
        task.call( target );
        return target;
    }

    protected void computeModificationMasks( InternalKnowledgeBase knowledgeBase ) {
        List<String> settableProperties = getSettableProperties( target, knowledgeBase );
        modificationMask = PropertySpecificUtil.getEmptyPropertyReactiveMask( settableProperties.size() );

        if ( with != null ) {
            List<String>[] inverseSettableProperties = new List[ with.length ];
            for ( int j = 0; j < with.length; j++ ) {
                inverseSettableProperties[ j ] = getSettableProperties( with[ j ], knowledgeBase );
                extraMasks[ j ] = PropertySpecificUtil.getEmptyPropertyReactiveMask( inverseSettableProperties[ j ].size() );
                task.computeModificationMasks( modificationMask, settableProperties, with, extraMasks, inverseSettableProperties );
            }
        } else {
            task.computeModificationMasks( modificationMask, settableProperties, null, null, null );
        }
    }

    protected List<String> getSettableProperties( Object o, InternalKnowledgeBase knowledgeBase ) {
        if ( knowledgeBase != null ) {
            return PropertySpecificUtil.getSettableProperties( knowledgeBase, o.getClass() );
        } else {
            return ClassUtils.getSettableProperties( o.getClass() );
        }
    }


    public BitMask getModificationMask() {
        return modificationMask;
    }

    public abstract Class getModificationClass();

    protected <R,C> void addTask( MetaProperty<? extends Metadatable,R,C> p, C val ) {
        addTask( p, val, Lit.SET );
    }
    protected <R,C> void addTask( MetaProperty<? extends Metadatable,R,C> p, C val, Lit mode ) {
        ModifyTaskLiteral<T,R,C> newTask = new ModifyTaskLiteral<T,R,C>( p, val, mode );
        if ( task == null ) {
            task = newTask;
        } else {
            ModifyTaskLiteral<T,?,?> lastTask = task;
            while ( lastTask.nextTask != null ) {
                lastTask = lastTask.nextTask;
            }
            lastTask.nextTask = newTask;
        }
    }

    @Override
    public URI getUri() {
        if ( key == null ) {
            key = createURI();
        }
        return key;
    }

    @Override
    public Object getId() {
        return getUri();
    }

    protected URI createURI() {
        StringBuilder sb = new StringBuilder();

        sb.append( MetadataContainer.getIdentifier( target ) );

        sb.append( "/modify" );
        ModifyTaskLiteral<T,?,?> t = task;
        while ( t != null ) {
            sb.append( "?" ).append( t.propertyLiteral.getName() );
            t = t.nextTask;
        }

        return URI.create( sb.toString() );
    }




    public class ModifyTaskLiteral<T extends Metadatable,R,C> implements ModifyTask, Serializable {
        protected MetaProperty<T,R,C> propertyLiteral;
        protected C value;
        protected Lit mode;
        protected ModifyTaskLiteral<T,?,?> nextTask;

        protected ModifyTaskLiteral( MetaProperty<? extends Metadatable,R,C> p, C val, Lit mode ) {
            this.propertyLiteral = (MetaProperty<T,R,C>) p;
            this.mode = mode;
            this.value = val;
        }

        public void call( T target ) {
            if ( propertyLiteral.isManyValued() ) {
                propertyLiteral.asManyValuedProperty().set( target, (List<R>) value, mode );
            } else {
                propertyLiteral.asFunctionalProperty().set( target, value, Lit.SET );
            }
            if ( nextTask != null ) {
                nextTask.call( target );
            }
        }

        public void computeModificationMasks( BitMask mask, List<String> settableProperties, Object[] with, BitMask[] extraMasks, List<String>[] inverseSettableProperties ) {
            if ( nextTask != null ) {
                nextTask.computeModificationMasks( mask, settableProperties, with, extraMasks, inverseSettableProperties );
            }

            setPropertyOnMask( mask, settableProperties, propertyLiteral.getName() );

            if ( with != null ) {
                for ( int j = 0; j < with.length; j++ ) {
                    if ( value == with[ j ] && propertyLiteral instanceof InvertibleMetaProperty ) {
                        setPropertyOnMask( extraMasks[ j ], inverseSettableProperties[ j ], ( (InvertibleMetaProperty) propertyLiteral ).getInverse().getName() );
                    }
                }
            }
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            ModifyTaskLiteral that = (ModifyTaskLiteral) o;

            if ( nextTask != null ? !nextTask.equals( that.nextTask ) : that.nextTask != null ) return false;
            if ( !propertyLiteral.equals( that.propertyLiteral ) ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = propertyLiteral.hashCode();
            result = 31 * result + ( nextTask != null ? nextTask.hashCode() : 0 );
            return result;
        }

        @Override
        public MetaProperty getProperty() {
            return propertyLiteral;
        }

        @Override
        public Object getValue() {
            return value;
        }

        public ModifyTask getNext() {
            return nextTask;
        }
    }
}
