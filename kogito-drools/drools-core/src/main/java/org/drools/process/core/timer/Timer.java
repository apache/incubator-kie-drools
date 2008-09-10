package org.drools.process.core.timer;

import java.util.Date;

import org.drools.time.JobHandle;

public class Timer {

    private long id;
    private long delay;
    private long period;
    // TODO separate in Timer and TimerInstance
    private JobHandle jobHandle;
    private Date activated;
    private Date lastTriggered;
    private long processInstanceId;
    
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
    
    public Date getActivated() {
		return activated;
	}

	public void setActivated(Date activated) {
		this.activated = activated;
	}

	public void setLastTriggered(Date lastTriggered) {
    	this.lastTriggered = lastTriggered;
    }
    
    public Date getLastTriggered() {
    	return lastTriggered;
    }

	public long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
    
}
