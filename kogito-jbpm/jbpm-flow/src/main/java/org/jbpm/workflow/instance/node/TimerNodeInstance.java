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

package org.jbpm.workflow.instance.node;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
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

    private String resolveVariable(String s) {
        Map<String, String> replacements = new HashMap<String, String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                    String variableValueString = variableValue == null ? "" : variableValue.toString(); 
                    replacements.put(paramName, variableValueString);
                } else {
                    try {
                        Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new NodeInstanceResolverFactory(this));
                        String variableValueString = variableValue == null ? "" : variableValue.toString();
                        replacements.put(paramName, variableValueString);
                    } catch (Throwable t) {
                        logger.error("Could not find variable scope for variable {}", paramName);
                        logger.error("when trying to replace variable in processId for sub process {}", getNodeName());
                        logger.error("Continuing without setting process id.");
                    }
                }
            }
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        return s;
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
