package org.drools.core.time.impl;

import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;

public interface TimerJobInstance {
    JobHandle getJobHandle();
    
    Job getJob();
    
    Trigger getTrigger();
    
    JobContext getJobContext();
    
    void cancel();
    boolean isCanceled();
}
