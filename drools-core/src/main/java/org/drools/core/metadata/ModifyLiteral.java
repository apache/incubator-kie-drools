package org.drools.core.metadata;

import org.drools.core.util.BitMaskUtil;

import java.net.URI;

public abstract class ModifyLiteral<T extends Metadatable> implements Modify<T> {
    private T target;
    private ModifyTaskLiteral<T,?> task;
    private long modificationMask;
    private URI key;

    protected abstract MetaClass<T> getMetaClassInfo();

    public ModifyLiteral( T target ) {
        this.target = target;
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

    public long getModificationMask() {
        return modificationMask;
    }

    public abstract Class getModificationClass();

    protected <R> void addTask( MetaProperty<? extends Metadatable,R> p, R val ) {
        ModifyTaskLiteral<T,R> newTask = new ModifyTaskLiteral<T, R>( p, val );
        if ( task == null ) {
            task = newTask;
        } else {
            ModifyTaskLiteral<T,?> lastTask = task;
            while ( task.nextTask != null ) {
                lastTask = task.nextTask;
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
        ModifyTaskLiteral<T,?> t = task;
        while ( t != null ) {
            sb.append( "?" ).append( t.propertyLiteral.getName() );
            t = t.nextTask;
        }

        return URI.create( sb.toString() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ModifyLiteral that = (ModifyLiteral) o;

        if ( !target.equals( that.target ) ) {
            return false;
        }
        if ( !task.equals( that.task ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return target.hashCode() ^ task.hashCode();
    }

    public class ModifyTaskLiteral<T extends Metadatable,R> implements ModifyTask {
        protected MetaProperty<T,R> propertyLiteral;
        protected R value;
        protected ModifyTaskLiteral<T,?> nextTask;

        protected ModifyTaskLiteral( MetaProperty<? extends Metadatable,R> p, R val ) {
            propertyLiteral = (MetaProperty<T, R>) p;
            value = val;
        }

        public void call( T target ) {
            propertyLiteral.set( target, value );
            if ( nextTask != null ) {
                nextTask.call( target );
            }
        }

        public long getModificationMask() {
            long downstreamMask = nextTask != null ? nextTask.getModificationMask() : 0;
            return BitMaskUtil.set( downstreamMask, getMetaClassInfo().getPropertyIndex( propertyLiteral ) );
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
