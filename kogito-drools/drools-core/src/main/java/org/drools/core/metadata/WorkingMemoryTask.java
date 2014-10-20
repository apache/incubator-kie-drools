package org.drools.core.metadata;

public interface WorkingMemoryTask<T> extends MetaCallableTask<T>, Identifiable {

    public Object getTargetId();

    public Modify getSetters();

    public Object getTarget();
}
