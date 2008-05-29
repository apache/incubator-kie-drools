package org.drools.time;

public interface JobContext {    
    /**
     * This method shoud only be called by the scheduler
     */    
    public void setJobHandle(JobHandle jobHandle);
    
    public JobHandle getJobHandle();
}
