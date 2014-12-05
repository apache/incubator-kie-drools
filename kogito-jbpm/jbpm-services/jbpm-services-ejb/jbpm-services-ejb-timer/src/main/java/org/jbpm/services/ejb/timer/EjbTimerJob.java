package org.jbpm.services.ejb.timer;

import java.io.Serializable;

import org.drools.core.time.impl.TimerJobInstance;

public class EjbTimerJob implements Serializable {

	private static final long serialVersionUID = -1299880116250779151L;
	private TimerJobInstance timerJobInstance;

	public EjbTimerJob(TimerJobInstance timerJobInstance) {
		this.timerJobInstance = timerJobInstance;
	}

	public TimerJobInstance getTimerJobInstance() {
		return timerJobInstance;
	}

	public void setTimerJobInstance(TimerJobInstance timerJobInstance) {
		this.timerJobInstance = timerJobInstance;
	}

	@Override
	public String toString() {
		return "EjbTimerJob [timerJobInstance=" + timerJobInstance + "]";
	}
}
