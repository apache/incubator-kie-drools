package org.drools.workflow.instance.node;

import org.drools.process.core.timer.Timer;
import org.drools.process.instance.EventListener;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.NodeInstance;
import org.drools.process.instance.timer.TimerInstance;
import org.drools.workflow.core.node.TimerNode;

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
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A TimerNode only accepts default incoming connections!");
        }
        TimerInstance timer = createTimerInstance();
        if (getTimerInstances() == null) {
        	addTimerListener();
        }
        ((InternalProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager()
            .registerTimer(timer, getProcessInstance());
        timerId = timer.getId();
    }
    
    protected TimerInstance createTimerInstance() {
    	Timer timer = getTimerNode().getTimer(); 
    	TimerInstance timerInstance = new TimerInstance();
    	timerInstance.setDelay(timer.getDelay());
    	timerInstance.setPeriod(timer.getPeriod());
    	timerInstance.setTimerId(timer.getId());
    	return timerInstance;
    }

    public void signalEvent(String type, Object event) {
    	if ("timerTriggered".equals(type)) {
    		TimerInstance timer = (TimerInstance) event;
            if (timer.getId() == timerId) {
                triggerCompleted(timer.getPeriod() == 0);
            }
    	}
    }
    
    public String[] getEventTypes() {
    	return new String[] { "timerTriggered" };
    }
    
    public void triggerCompleted(boolean remove) {
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, remove);
    }
    
    public void cancel() {
    	((InternalProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager().cancelTimer(timerId);
        super.cancel();
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        if (getTimerInstances() == null) {
        	((InternalProcessInstance) getProcessInstance()).addEventListener("timerTriggered", this, false);
        }
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        ((InternalProcessInstance) getProcessInstance()).removeEventListener("timerTriggered", this, false);
    }

}
