package org.drools.time.impl;

import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;

public interface TimerJobInstance {
    public JobHandle getJobHandle();
    
    public Job getJob();
    
    public Trigger getTrigger();
    
    public JobContext getJobContext();
    
    
}
