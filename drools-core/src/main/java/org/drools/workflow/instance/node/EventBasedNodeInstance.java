package org.drools.workflow.instance.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.process.core.timer.Timer;
import org.drools.process.instance.EventListener;
import org.drools.process.instance.timer.TimerInstance;
import org.drools.process.instance.timer.TimerManager;
import org.drools.spi.KnowledgeHelper;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventBasedNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.ExtendedNodeInstanceImpl;

public abstract class EventBasedNodeInstance extends ExtendedNodeInstanceImpl implements EventBasedNodeInstanceInterface, EventListener {
	
	private static final long serialVersionUID = 4L;

	private List<Long> timerInstances;

	public EventBasedNode getEventBasedNode() {
        return (EventBasedNode) getNode();
    }
    
	public void internalTrigger(NodeInstance from, String type) {
		super.internalTrigger(from, type);
		// activate timers
		Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
		if (timers != null) {
			addTimerListener();
			timerInstances = new ArrayList<Long>(timers.size());
			TimerManager timerManager = getProcessInstance().getWorkingMemory().getTimerManager();
			for (Timer timer: timers.keySet()) {
				TimerInstance timerInstance = createTimerInstance(timer); 
				timerManager.registerTimer(timerInstance, getProcessInstance());
				timerInstances.add(timerInstance.getId());
			}
		}
	}
	
    protected TimerInstance createTimerInstance(Timer timer) {
    	TimerInstance timerInstance = new TimerInstance();
    	timerInstance.setDelay(timer.getDelay());
    	timerInstance.setPeriod(timer.getPeriod());
    	timerInstance.setTimerId(timer.getId());
    	return timerInstance;
    }

    public void signalEvent(String type, Object event) {
    	if ("timerTriggered".equals(type)) {
    		TimerInstance timerInstance = (TimerInstance) event;
            if (timerInstances.contains(timerInstance.getId())) {
                triggerTimer(timerInstance);
            }
    	}
    }
    
    private void triggerTimer(TimerInstance timerInstance) {
    	for (Map.Entry<Timer, DroolsAction> entry: getEventBasedNode().getTimers().entrySet()) {
    		if (entry.getKey().getId() == timerInstance.getTimerId()) {
    			KnowledgeHelper knowledgeHelper = createKnowledgeHelper();
    			executeAction(entry.getValue(), knowledgeHelper);
    			return;
    		}
    	}
    }
    
    public String[] getEventTypes() {
    	return new String[] { "timerTriggered" };
    }
    
    public void triggerCompleted() {
        triggerCompleted(Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
    public void addEventListeners() {
    	if (timerInstances != null && timerInstances.size() > 0) {
    		addTimerListener();
    	}
    }
    
    protected void addTimerListener() {
    	getProcessInstance().addEventListener("timerTriggered", this, false);
    }
    
    public void removeEventListeners() {
        getProcessInstance().removeEventListener("timerTriggered", this, false);
    }

	protected void triggerCompleted(String type, boolean remove) {
		cancelTimers();
		super.triggerCompleted(type, remove);
	}
	
	public List<Long> getTimerInstances() {
		return timerInstances;
	}
	
	public void internalSetTimerInstances(List<Long> timerInstances) {
		this.timerInstances = timerInstances;
	}

    public void cancel() {
        cancelTimers();
        removeEventListeners();
        super.cancel();
    }
    
	private void cancelTimers() {
		// deactivate still active timers
		if (timerInstances != null) {
			TimerManager timerManager = getProcessInstance().getWorkingMemory().getTimerManager();
			for (Long id: timerInstances) {
				timerManager.cancelTimer(id);
			}
		}
	}
	
}
