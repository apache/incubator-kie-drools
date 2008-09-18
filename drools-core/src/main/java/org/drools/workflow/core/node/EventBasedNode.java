package org.drools.workflow.core.node;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.DroolsAction;

public class EventBasedNode extends SequenceNode {

    private static final long serialVersionUID = 400L;

	private Map<Timer, DroolsAction> timers;
	
	public Map<Timer, DroolsAction> getTimers() {
		return timers;
	}
	
	public void addTimer(Timer timer, DroolsAction action) {
		if (timers == null) {
			timers = new HashMap<Timer, DroolsAction>();
		}
		if (timer.getId() == 0) {
			long id = 0;
	        for (Timer t: timers.keySet()) {
	            if (t.getId() > id) {
	                id = t.getId();
	            }
	        }
	        timer.setId(++id);
		}
		timers.put(timer, action);
	}
	
	public void internalSetTimers(Map<Timer, DroolsAction> timers) {
		this.timers = timers;
	}

}
