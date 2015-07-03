package org.drools.core.time;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.time.impl.TimerJobInstance;

import java.util.Map;

public class SelfRemovalJobContext implements JobContext {
    protected final JobContext jobContext;
    protected final Map<Long, TimerJobInstance> timerInstances;

    public SelfRemovalJobContext( JobContext jobContext,
                                  Map<Long, TimerJobInstance> timerInstances ) {
        this.jobContext = jobContext;
        this.timerInstances = timerInstances;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    @Override
    public void setJobHandle(JobHandle jobHandle) {
        jobContext.setJobHandle( jobHandle );
    }

    @Override
    public JobHandle getJobHandle() {
        return jobContext.getJobHandle();
    }

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return jobContext.getWorkingMemory();
    }

    public void remove() {
        this.timerInstances.remove( jobContext.getJobHandle().getId() );
    }
}
