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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.process.core.timer.Timer;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.timer.TimerInstance;
import org.drools.process.instance.timer.TimerManager;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstance;
import org.drools.spi.KnowledgeHelper;
import org.drools.time.TimeUtils;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.node.StateBasedNode;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.ExtendedNodeInstanceImpl;

public abstract class StateBasedNodeInstance extends ExtendedNodeInstanceImpl implements EventBasedNodeInstanceInterface, EventListener {
	
	private static final long serialVersionUID = 510l;

	private List<Long> timerInstances;

	public StateBasedNode getEventBasedNode() {
        return (StateBasedNode) getNode();
    }
    
	public void internalTrigger(NodeInstance from, String type) {
		super.internalTrigger(from, type);
		// activate timers
		Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
		if (timers != null) {
			addTimerListener();
			timerInstances = new ArrayList<Long>(timers.size());
			TimerManager timerManager = ((ProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager();
			for (Timer timer: timers.keySet()) {
				TimerInstance timerInstance = createTimerInstance(timer); 
				timerManager.registerTimer(timerInstance, (ProcessInstance) getProcessInstance());
				timerInstances.add(timerInstance.getId());
			}
		}
	}
	
    protected TimerInstance createTimerInstance(Timer timer) {
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
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
    public void addEventListeners() {
    	if (timerInstances != null && timerInstances.size() > 0) {
    		addTimerListener();
    	}
    }
    
    protected void addTimerListener() {
    	((WorkflowProcessInstance) getProcessInstance()).addEventListener("timerTriggered", this, false);
    }
    
    public void removeEventListeners() {
    	((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timerTriggered", this, false);
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
			TimerManager timerManager = ((ProcessInstance) getProcessInstance()).getWorkingMemory().getTimerManager();
			for (Long id: timerInstances) {
				timerManager.cancelTimer(id);
			}
		}
	}
	
}
