package org.drools.workflow.instance.node;

import org.drools.process.core.timer.Timer;
import org.drools.process.instance.EventListener;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.instance.NodeInstance;

public class TimerNodeInstance extends EventBasedNodeInstance implements EventListener {

    private static final long serialVersionUID = 400L;
    
    private long timerId;
    
    public TimerNode getTimerNode() {
        return (TimerNode) getNode();
    }
    
    public long getTimerId() {
    	return timerId;
    }
    
    public void internalSetTimerId(long timerId) {
    	this.timerId = timerId;
    }

    public void internalTrigger(NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A TimerNode only accepts default incoming connections!");
        }
        Timer timer = createTimer();
        addEventListeners();
        getProcessInstance().getWorkingMemory().getTimerManager()
            .registerTimer(timer, getProcessInstance());
        timerId = timer.getId();
    }
    
    protected Timer createTimer() {
    	Timer timerDef = getTimerNode().getTimer(); 
    	Timer timer = new Timer();
    	timer.setDelay(timerDef.getDelay());
    	timer.setPeriod(timerDef.getPeriod());
    	return timer;
    }

    public void signalEvent(String type, Object event) {
    	if ("timerTriggered".equals(type)) {
    		Timer timer = (Timer) event;
            if (timer.getId() == timerId) {
                triggerCompleted(timer.getPeriod() == 0);
            }
    	}
    }
    
    public void triggerCompleted(boolean remove) {
        triggerCompleted(Node.CONNECTION_DEFAULT_TYPE, remove);
    }
    
    public void cancel() {
        getProcessInstance().getWorkingMemory().getTimerManager()
            .cancelTimer(getTimerNode().getTimer());
        super.cancel();
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        getProcessInstance().addEventListener("timerTriggered", this);
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("timerTriggered", this);
    }

}
