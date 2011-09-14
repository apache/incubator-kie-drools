package org.drools.time;

import java.util.Map;

import org.drools.time.impl.TimerJobInstance;

public class SelfRemovalJobContext implements JobContext {
    private JobContext jobContext;
    private Map<Long, TimerJobInstance> timerInstances;
    
    public SelfRemovalJobContext(JobContext jobContext,
                                 Map<Long, TimerJobInstance> timerInstances) {
        this.jobContext = jobContext;
        this.timerInstances = timerInstances;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    public void setJobHandle(JobHandle jobHandle) {
        jobContext.setJobHandle( jobHandle );
    }

    public JobHandle getJobHandle() {
        return jobContext.getJobHandle();
    }

    public void remove() {
        this.timerInstances.remove( jobContext.getJobHandle().getId() );
    }
  
    
}
