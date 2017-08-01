/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.admin;

import java.util.Date;

import org.jbpm.services.api.admin.TimerInstance;

public class TimerInstanceImpl implements TimerInstance {

	private static final long serialVersionUID = 8843363575668976484L;

	private String timerName;
	private long timerId;
	private Date activationTime;
	private Date lastFireTime;
	private Date nextFireTime;
	
	private long delay;
	private long period;
	private int repeatLimit;
	
	private long processInstanceId;
	private long sessionId;
	
	public String getTimerName() {
		return timerName;
	}
	
	public void setTimerName(String timerName) {
		this.timerName = timerName;
	}
	
	public long getTimerId() {
		return timerId;
	}
	
	public void setTimerId(long timerId) {
		this.timerId = timerId;
	}
	
	public Date getActivationTime() {
		return activationTime;
	}
	
	public void setActivationTime(Date activationTime) {
		this.activationTime = activationTime;
	}
	
	public Date getLastFireTime() {
		return lastFireTime;
	}
	
	public void setLastFireTime(Date lastFireTime) {
		this.lastFireTime = lastFireTime;
	}
	
	public Date getNextFireTime() {
		return nextFireTime;
	}
	
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
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
	
	public int getRepeatLimit() {
		return repeatLimit;
	}
	
	public void setRepeatLimit(int repeatLimit) {
		this.repeatLimit = repeatLimit;
	}

	public long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((lastFireTime == null) ? 0 : lastFireTime.hashCode());
        result = prime * result + ((nextFireTime == null) ? 0 : nextFireTime.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + (int) (processInstanceId ^ (processInstanceId >>> 32));
        result = prime * result + repeatLimit;
        result = prime * result + (int) (sessionId ^ (sessionId >>> 32));
        result = prime * result + (int) (timerId ^ (timerId >>> 32));
        result = prime * result + ((timerName == null) ? 0 : timerName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimerInstanceImpl other = (TimerInstanceImpl) obj;
        if (activationTime == null) {
            if (other.activationTime != null)
                return false;
        } else if (!activationTime.equals(other.activationTime))
            return false;
        if (delay != other.delay)
            return false;
        if (lastFireTime == null) {
            if (other.lastFireTime != null)
                return false;
        } else if (!lastFireTime.equals(other.lastFireTime))
            return false;
        if (nextFireTime == null) {
            if (other.nextFireTime != null)
                return false;
        } else if (!nextFireTime.equals(other.nextFireTime))
            return false;
        if (period != other.period)
            return false;
        if (processInstanceId != other.processInstanceId)
            return false;
        if (repeatLimit != other.repeatLimit)
            return false;
        if (sessionId != other.sessionId)
            return false;
        if (timerId != other.timerId)
            return false;
        if (timerName == null) {
            if (other.timerName != null)
                return false;
        } else if (!timerName.equals(other.timerName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TimerInstanceImpl [timerName=" + timerName + ", timerId=" + timerId + ", activationTime=" + activationTime + 
                ", lastFireTime=" + lastFireTime + ", nextFireTime=" + nextFireTime + ", delay=" + delay + 
                ", period=" + period + ", repeatLimit=" + repeatLimit + ", processInstanceId=" + processInstanceId + 
                ", sessionId=" + sessionId + "]";
    }	
}
