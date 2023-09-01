package org.drools.core.time.impl;

import java.io.Serializable;

import org.drools.core.time.Job;

/**
 * A default implementation for the JobHandle interface
 */
public class DefaultJobHandle extends AbstractJobHandle<DefaultJobHandle> implements Serializable {

    private static final long serialVersionUID = 510l;

    private volatile boolean cancel = false;

    private long              id;

    private TimerJobInstance  timerJobInstance;

    public DefaultJobHandle(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void cancel() {
        if (!this.cancel) {
            timerJobInstance.cancel();
        }
        this.cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    protected Job getJob() {
        return timerJobInstance != null ? timerJobInstance.getJob() : null;
    }

    public void setTimerJobInstance(TimerJobInstance scheduledJob) {
        this.timerJobInstance = scheduledJob;
    }

    public TimerJobInstance getTimerJobInstance() {
        return this.timerJobInstance;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getJob() == null) ? 0 : getJob().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final DefaultJobHandle other = (DefaultJobHandle) obj;
        if ( getJob() == null ) {
            if ( other.getJob() != null ) return false;
        } else if ( !getJob().equals( other.getJob() ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "JobHandle #" + id;
    }
}
