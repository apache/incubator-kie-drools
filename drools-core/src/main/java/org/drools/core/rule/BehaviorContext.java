package org.drools.core.rule;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.time.JobHandle;

import java.util.Collection;

public interface BehaviorContext {
    Collection<DefaultEventHandle> getFactHandles();

    default JobHandle getJobHandle() {
        return null;
    }

    default void setJobHandle(JobHandle jobHandle) {
        throw new UnsupportedOperationException();
    }
}
