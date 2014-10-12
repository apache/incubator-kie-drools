package org.drools.core.metadata;

import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.bitmask.BitMask;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.setPropertyOnMask;

public abstract class ModifyLiteral<T extends Metadatable> extends AbstractWMTask<T> implements Modify<T> {
    private T target;
    private ModifyTaskLiteral<T,?,?> task;
    private BitMask modificationMask;
    private URI key;
    private Object[] with;

    protected abstract MetaClass<T> getMetaClassInfo();

    public ModifyLiteral( T target, With[] with ) {
        this.target = target;
        switch ( with.length ) {
            case 0 : this.with = null;
                break;
            case 1 : this.with = with[ 0 ].getArgs();
                break;
            default :
                mergeWiths( with );
        }
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
    public KIND kind() {
        return KIND.MODIFY;
    }

    public T call( T object ) {
        setTarget( object );
        return call();
    }

    public T call() {
        modificationMask = task.getModificationMask();
        task.call( target );
        return target;
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




    public class ModifyTaskLiteral<T extends Metadatable,R,C> implements ModifyTask {
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

        public BitMask getModificationMask() {
            BitMask downstreamMask = nextTask != null ? nextTask.getModificationMask() : getEmptyPropertyReactiveMask( getMetaClassInfo().getProperties().length );
            return setPropertyOnMask( downstreamMask, getMetaClassInfo().getPropertyIndex( propertyLiteral ) );
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
