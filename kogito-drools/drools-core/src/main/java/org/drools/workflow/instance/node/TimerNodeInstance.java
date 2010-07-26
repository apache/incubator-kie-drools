/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workflow.instance.node;

import org.drools.process.core.timer.Timer;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.timer.TimerInstance;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstance;
import org.drools.time.TimeUtils;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.instance.WorkflowProcessInstance;

public class TimerNodeInstance extends StateBasedNodeInstance implements EventListener {

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
        ((ProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager()
            .registerTimer(timer, (ProcessInstance) getProcessInstance());
        timerId = timer.getId();
    }
    
    protected TimerInstance createTimerInstance() {
    	Timer timer = getTimerNode().getTimer(); 
    	TimerInstance timerInstance = new TimerInstance();
    	timerInstance.setDelay(TimeUtils.parseTimeString(timer.getDelay()));
    	if (timer.getPeriod() == null) {
    		timerInstance.setPeriod(0);
    	} else {
    		timerInstance.setPeriod(TimeUtils.parseTimeString(timer.getPeriod()));
    	}
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
    	((ProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager().cancelTimer(timerId);
        super.cancel();
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        if (getTimerInstances() == null) {
        	addTimerListener();
        }
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        ((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timerTriggered", this, false);
    }

}
