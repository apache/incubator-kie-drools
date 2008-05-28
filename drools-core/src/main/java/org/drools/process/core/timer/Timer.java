package org.drools.process.core.timer;

import org.drools.scheduler.JobHandle;

public class Timer {

    private long id;
    private long delay;
    private long period;
    private JobHandle jobHandle;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getDelay() {
        return delay;
    }
    
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    public long getPeriod() {
        return period;
    }
    
    public void setPeriod(long period) {
        this.period = period;
    }

    public JobHandle getJobHandle() {
        return jobHandle;
    }

    public void setJobHandle(JobHandle jobHandle) {
        this.jobHandle = jobHandle;
    }
    
    
    
}
