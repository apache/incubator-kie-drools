package org.drools.core.metadata;

public abstract class AbstractWMTask<T> implements WorkingMemoryTask<T> {

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( ! ( o instanceof WorkingMemoryTask ) ) return false;

        WorkingMemoryTask that = (WorkingMemoryTask) o;

        return getTargetId().equals( that.getTargetId() );
    }

    @Override
    public int hashCode() {
        return getTargetId().hashCode();
    }
}
