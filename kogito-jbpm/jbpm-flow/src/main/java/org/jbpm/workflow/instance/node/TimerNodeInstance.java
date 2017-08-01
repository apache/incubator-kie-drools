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

package org.jbpm.workflow.instance.node;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerNodeInstance extends StateBasedNodeInstance implements EventListener {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(TimerNodeInstance.class);
    
    private long timerId;
    private TimerInstance timerInstance;
    
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
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A TimerNode only accepts default incoming connections!");
        }
        InternalKnowledgeRuntime kruntime =  getProcessInstance().getKnowledgeRuntime();
        timerInstance = createTimerInstance(kruntime);
        if (getTimerInstances() == null) {
        	addTimerListener();
        }
        ((InternalProcessRuntime)kruntime.getProcessRuntime())
        	.getTimerManager().registerTimer(timerInstance, (ProcessInstance) getProcessInstance());
        timerId = timerInstance.getId();
    }
    
    protected TimerInstance createTimerInstance(InternalKnowledgeRuntime kruntime) {
    	Timer timer = getTimerNode().getTimer(); 
    	TimerInstance timerInstance = new TimerInstance();
    	
    	if (kruntime != null && kruntime.getEnvironment().get("jbpm.business.calendar") != null){
        	BusinessCalendar businessCalendar = (BusinessCalendar) kruntime.getEnvironment().get("jbpm.business.calendar");
        	
        	String delay = resolveVariable(timer.getDelay());
        	
        	timerInstance.setDelay(businessCalendar.calculateBusinessTimeAsDuration(delay));
        	
        	if (timer.getPeriod() == null) {
                timerInstance.setPeriod(0);
            } else {
                String period = resolveVariable(timer.getPeriod());
                timerInstance.setPeriod(businessCalendar.calculateBusinessTimeAsDuration(period));
            }
    	} else {
    	    configureTimerInstance(timer, timerInstance);
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
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, remove);
    }
    
    public void cancel() {
    	((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime()
			.getProcessRuntime()).getTimerManager().cancelTimer(timerId);
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

    public TimerInstance getTimerInstance() {
        return timerInstance;
    }

}
