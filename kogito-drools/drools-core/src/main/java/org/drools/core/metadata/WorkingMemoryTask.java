package org.drools.core.metadata;

import java.io.Serializable;

public interface WorkingMemoryTask<T> extends MetaCallableTask<T>, Identifiable, Serializable {

    public Object getTargetId();

    public Modify getSetters();

    public Object getTarget();
}
