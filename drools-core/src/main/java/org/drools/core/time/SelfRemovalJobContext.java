package org.drools.core.time;

import java.util.Map;

import org.drools.base.time.JobHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.time.impl.TimerJobInstance;

public class SelfRemovalJobContext implements JobContext {

    private static final long serialVersionUID = 614425985040796356L;

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
    public ReteEvaluator getReteEvaluator() {
        return jobContext.getReteEvaluator();
    }

    public void remove() {
        this.timerInstances.remove( jobContext.getJobHandle().getId() );
    }
}
