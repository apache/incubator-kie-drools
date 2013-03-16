package org.drools.core.time.impl;

import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;

public interface TimerJobInstance {
    public JobHandle getJobHandle();
    
    public Job getJob();
    
    public Trigger getTrigger();
    
    public JobContext getJobContext();
    
    
}
