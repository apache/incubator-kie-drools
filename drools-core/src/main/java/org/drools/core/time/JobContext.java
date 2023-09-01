package org.drools.core.time;

import java.io.Serializable;
import java.util.Optional;

import org.drools.base.time.JobHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;

public interface JobContext extends Serializable {
    /**
     * This method should only be called by the scheduler
     */    
    void setJobHandle(JobHandle jobHandle);

    JobHandle getJobHandle();

    ReteEvaluator getReteEvaluator();

    default Optional<InternalKnowledgeRuntime> getInternalKnowledgeRuntime() {
        return getReteEvaluator() instanceof InternalWorkingMemory ? Optional.ofNullable(((InternalWorkingMemory)getReteEvaluator()).getKnowledgeRuntime()) : Optional.empty();
    }
}
