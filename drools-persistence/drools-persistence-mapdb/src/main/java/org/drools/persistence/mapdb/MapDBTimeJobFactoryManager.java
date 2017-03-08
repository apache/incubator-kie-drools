package org.drools.persistence.mapdb;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.ThreadSafeTrackableTimeJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;
import org.kie.api.runtime.ExecutableRunner;

public class MapDBTimeJobFactoryManager 
		extends ThreadSafeTrackableTimeJobFactoryManager
		implements CommandServiceTimerJobFactoryManager{

    private ExecutableRunner runner;

    public void setRunner(ExecutableRunner runner) {
        this.runner = runner;
    }

    public ExecutableRunner getRunner() {
        return runner;
    }

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );

        return new MapDBTimerJobInstance( new SelfRemovalJob( job ),
                                        createJobContext( ctx ),
                                        trigger,
                                        handle,
                                        scheduler );
    }
}
