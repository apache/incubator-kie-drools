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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.time.TimeUtils;
import org.drools.core.time.impl.CronExpression;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StateBasedNodeInstance extends ExtendedNodeInstanceImpl implements EventBasedNodeInstanceInterface, EventListener {

	private static final long serialVersionUID = 510l;

    private static final Logger logger = LoggerFactory.getLogger(StateBasedNodeInstance.class);

	private List<Long> timerInstances;

	public StateBasedNode getEventBasedNode() {
        return (StateBasedNode) getNode();
    }

	public void internalTrigger(NodeInstance from, String type) {
		super.internalTrigger(from, type);
		// if node instance was cancelled, abort
		if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
			return;
		}
		// activate timers
		Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
		if (timers != null) {
			addTimerListener();
			timerInstances = new ArrayList<Long>(timers.size());
			TimerManager timerManager = ((InternalProcessRuntime)
				getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getTimerManager();
			for (Timer timer: timers.keySet()) {
				TimerInstance timerInstance = createTimerInstance(timer);
				timerManager.registerTimer(timerInstance, (ProcessInstance) getProcessInstance());
				timerInstances.add(timerInstance.getId());
			}
		}

		if (getEventBasedNode().getBoundaryEvents() != null) {

		    for (String name : getEventBasedNode().getBoundaryEvents()) {

                boolean isActive = ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
                    .isRuleActiveInRuleFlowGroup("DROOLS_SYSTEM", name, getProcessInstance().getId());
                if (isActive) {
                    getProcessInstance().getKnowledgeRuntime().signalEvent(name, null);
                } else {
                    addActivationListener();
                }
		    }
		}		
		
		((WorkflowProcessInstanceImpl) getProcessInstance()).addActivatingNodeId((String) getNode().getMetaData().get("UniqueId"));
	}
	
	protected void configureSla() {
	    String slaDueDateExpression = (String) getNode().getMetaData().get("customSLADueDate");
        if (slaDueDateExpression != null) {
            TimerInstance timer = ((WorkflowProcessInstanceImpl)getProcessInstance()).configureSLATimer(slaDueDateExpression);
            if (timer != null) {
                this.slaTimerId = timer.getId();
                this.slaDueDate = new Date(System.currentTimeMillis() + timer.getDelay());
                this.slaCompliance = ProcessInstance.SLA_PENDING;
                logger.debug("SLA for node instance {} is PENDING with due date {}", this.getId(), this.slaDueDate);
                addTimerListener();
            }
        }
	}

    protected TimerInstance createTimerInstance(Timer timer) {
    	TimerInstance timerInstance = new TimerInstance();
		KieRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
    	if (kruntime != null && kruntime.getEnvironment().get("jbpm.business.calendar") != null){
        	BusinessCalendar businessCalendar = (BusinessCalendar) kruntime.getEnvironment().get("jbpm.business.calendar");
        	String delay = null;
        	switch (timer.getTimeType()) {
            case Timer.TIME_CYCLE:

            	if (CronExpression.isValidExpression(timer.getDelay())) {
            		timerInstance.setCronExpression(timer.getDelay());
            	} else {

	            	String tempDelay = resolveVariable(timer.getDelay());
	            	String tempPeriod = resolveVariable(timer.getPeriod());
	            	if (DateTimeUtils.isRepeatable(tempDelay)) {
	            		String[] values = DateTimeUtils.parseISORepeatable(tempDelay);
	            		String tempRepeatLimit = values[0];
	            		tempDelay = values[1];
	            		tempPeriod = values[2];

	            		if (!tempRepeatLimit.isEmpty()) {
	            			try {
	            				int repeatLimit = Integer.parseInt(tempRepeatLimit);
	            				if (repeatLimit > -1) {
	            					timerInstance.setRepeatLimit(repeatLimit+1);
	            				}
	            			} catch (NumberFormatException e) {
	            				// ignore
	            			}
	            		}
	            	}


	            	timerInstance.setDelay(businessCalendar.calculateBusinessTimeAsDuration(tempDelay));

	            	if (tempPeriod == null) {
	                    timerInstance.setPeriod(0);
	                } else {
	                    timerInstance.setPeriod(businessCalendar.calculateBusinessTimeAsDuration(tempPeriod));
	                }
            	}
                break;
            case Timer.TIME_DURATION:
            	delay = resolveVariable(timer.getDelay());

            	timerInstance.setDelay(businessCalendar.calculateBusinessTimeAsDuration(delay));
            	timerInstance.setPeriod(0);
            	break;
            case Timer.TIME_DATE:
            	// even though calendar is available concrete date was provided so it shall be used
            	configureTimerInstance(timer, timerInstance);
            default:
                break;
            }

    	} else {
    	    configureTimerInstance(timer, timerInstance);
    	}
    	timerInstance.setTimerId(timer.getId());
    	return timerInstance;
    }

    protected void configureTimerInstance(Timer timer, TimerInstance timerInstance) {
        String s = null;
        long duration = -1;
        switch (timer.getTimeType()) {
        case Timer.TIME_CYCLE:
            if (timer.getPeriod() != null) {
                timerInstance.setDelay(resolveValue(timer.getDelay()));
                if (timer.getPeriod() == null) {
                    timerInstance.setPeriod(0);
                } else {
                    timerInstance.setPeriod(resolveValue(timer.getPeriod()));
                }
            } else {
                String resolvedDelay = resolveVariable(timer.getDelay());
            	if (CronExpression.isValidExpression(resolvedDelay)) {
            		timerInstance.setCronExpression(resolvedDelay);
            	} else {

	                // when using ISO date/time period is not set
	                long[] repeatValues = null;
	                try {
	                    repeatValues = DateTimeUtils.parseRepeatableDateTime(timer.getDelay());
	                } catch (RuntimeException e) {
	                    // cannot parse delay, trying to interpret it	                   
	                    repeatValues = DateTimeUtils.parseRepeatableDateTime(resolvedDelay);
	                }
	                if (repeatValues.length == 3) {
	                    int parsedReapedCount = (int)repeatValues[0];
	                    if (parsedReapedCount > -1) {
	                        timerInstance.setRepeatLimit(parsedReapedCount+1);
	                    }
	                    timerInstance.setDelay(repeatValues[1]);
	                    timerInstance.setPeriod(repeatValues[2]);
	                }else if (repeatValues.length == 2) {
	                    timerInstance.setDelay(repeatValues[0]);
	                    timerInstance.setPeriod(repeatValues[1]);
	                } else {
	                    timerInstance.setDelay(repeatValues[0]);
	                    timerInstance.setPeriod(0);
	                }
            	}
            }
            break;
        case Timer.TIME_DURATION:

            try {
                duration = DateTimeUtils.parseDuration(timer.getDelay());
            } catch (RuntimeException e) {
                // cannot parse delay, trying to interpret it
                s = resolveVariable(timer.getDelay());
                duration = DateTimeUtils.parseDuration(s);
            }
            timerInstance.setDelay(duration);
            timerInstance.setPeriod(0);
            break;
        case Timer.TIME_DATE:
            try {
                duration = DateTimeUtils.parseDateAsDuration(timer.getDate());
            } catch (RuntimeException e) {
                // cannot parse delay, trying to interpret it
                s = resolveVariable(timer.getDate());
                duration = DateTimeUtils.parseDateAsDuration(s);
            }
            timerInstance.setDelay(duration);
            timerInstance.setPeriod(0);
            break;

        default:
            break;
        }

    }

    private long resolveValue(String s) {
    	try {
    		return TimeUtils.parseTimeString(s);
    	} catch (RuntimeException e) {
    		s = resolveVariable(s);
            return TimeUtils.parseTimeString(s);
    	}
    }

    protected String resolveVariable(String s) {
    	if (s == null) {
    		return null;
    	}
    	// cannot parse delay, trying to interpret it
		Map<String, String> replacements = new HashMap<String, String>();
		Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
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
    
    protected void handleSLAViolation() {
        if (slaCompliance == ProcessInstance.SLA_PENDING) {
            InternalProcessRuntime processRuntime = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime());
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
            logger.debug("SLA violated on node instance {}", getId());                   
            this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            this.slaTimerId = -1;
            processRuntime.getProcessEventSupport().fireAfterSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
        }
    }

    @Override
    public void signalEvent(String type, Object event) {
    	if ("timerTriggered".equals(type)) {
    		TimerInstance timerInstance = (TimerInstance) event;
            if (timerInstances != null && timerInstances.contains(timerInstance.getId())) {
                triggerTimer(timerInstance);
            } else if (timerInstance.getId() == slaTimerId) {                
                handleSLAViolation();        
            }
    	} else if (("slaViolation:" + getId()).equals(type)) {
                           
            handleSLAViolation();        
           
        } else if (type.equals(getActivationType())) {
            if (event instanceof MatchCreatedEvent) {
                String name = ((MatchCreatedEvent)event).getMatch().getRule().getName();
                if (checkProcessInstance((Activation) ((MatchCreatedEvent)event).getMatch())) {
                    ((MatchCreatedEvent)event).getKieRuntime().signalEvent(name, null);
                }
            }
        }
    }

    private void triggerTimer(TimerInstance timerInstance) {
    	for (Map.Entry<Timer, DroolsAction> entry: getEventBasedNode().getTimers().entrySet()) {
    		if (entry.getKey().getId() == timerInstance.getTimerId()) {
    			executeAction((Action) entry.getValue().getMetaData("Action"));
    			return;
    		}
    	}
    }

    @Override
    public String[] getEventTypes() {
    	return new String[] { "timerTriggered", getActivationType()};
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }

    public void addEventListeners() {
    	if (timerInstances != null && timerInstances.size() > 0 || slaTimerId > -1) {
    		addTimerListener();
    	}
    	if (slaCompliance == ProcessInstance.SLA_PENDING) {
    	    ((WorkflowProcessInstance) getProcessInstance()).addEventListener("slaViolation:" + getId(), this, true);
    	}
    }

    protected void addTimerListener() {
    	((WorkflowProcessInstance) getProcessInstance()).addEventListener("timerTriggered", this, false);
    	((WorkflowProcessInstance) getProcessInstance()).addEventListener("timer", this, true);
    	((WorkflowProcessInstance) getProcessInstance()).addEventListener("slaViolation:" + getId(), this, true);
    }

    public void removeEventListeners() {
    	((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timerTriggered", this, false);
    	((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timer", this, true);
    	((WorkflowProcessInstance) getProcessInstance()).removeEventListener("slaViolation:" + getId(), this, true);
    }

	protected void triggerCompleted(String type, boolean remove) {
	    if (this.slaCompliance == ProcessInstance.SLA_PENDING) {
	        if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the node instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = ProcessInstance.STATE_COMPLETED;
            }
        }
	    cancelSlaTimer();
	    ((org.jbpm.workflow.instance.NodeInstanceContainer)getNodeInstanceContainer()).setCurrentLevel(getLevel());
		cancelTimers();
		removeActivationListener();
		super.triggerCompleted(type, remove);
	}

	public List<Long> getTimerInstances() {
		return timerInstances;
	}

	public void internalSetTimerInstances(List<Long> timerInstances) {
		this.timerInstances = timerInstances;
	}

    public void cancel() {
        if (this.slaCompliance == ProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the process instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = ProcessInstance.SLA_ABORTED;
            }
        }
        cancelSlaTimer();
        cancelTimers();
        removeEventListeners();
        removeActivationListener();
        super.cancel();
    }

	private void cancelTimers() {
		// deactivate still active timers
		if (timerInstances != null) {
			TimerManager timerManager = ((InternalProcessRuntime)
				getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getTimerManager();
			for (Long id: timerInstances) {
				timerManager.cancelTimer(id);
			}
		}
	}
	
	private void cancelSlaTimer() {
	    if (this.slaTimerId > -1) {
	        TimerManager timerManager = ((InternalProcessRuntime)
	                getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getTimerManager();
	        timerManager.cancelTimer(this.slaTimerId);
            logger.debug("SLA Timer {} has been canceled", this.slaTimerId);
        }
	}

	protected String getActivationType() {
	    return "RuleFlowStateEvent-" + this.getProcessInstance().getProcessId();
	}

    private void addActivationListener() {
        getProcessInstance().addEventListener(getActivationType(), this, true);
    }

    private void removeActivationListener() {
        getProcessInstance().removeEventListener(getActivationType(), this, true);
    }

    protected boolean checkProcessInstance(Activation activation) {
        final Map<?, ?> declarations = activation.getSubRule().getOuterDeclarations();
        for ( Iterator<?> it = declarations.values().iterator(); it.hasNext(); ) {
            Declaration declaration = (Declaration) it.next();
            if ("processInstance".equals(declaration.getIdentifier())
            		|| "org.kie.api.runtime.process.WorkflowProcessInstance".equals(declaration.getTypeName())) {
                Object value = declaration.getValue(
                    ((StatefulKnowledgeSessionImpl) getProcessInstance().getKnowledgeRuntime()).getInternalWorkingMemory(),
                    ((InternalFactHandle) activation.getTuple().get(declaration)).getObject());
                if (value instanceof ProcessInstance) {
                    return ((ProcessInstance) value).getId() == getProcessInstance().getId();
                }
            }
        }
        return true;
    }
    
    protected void mapDynamicOutputData(Map<String, Object> results) {
        if (results != null && !results.isEmpty()) {
            VariableScope variableScope = (VariableScope) ((ContextContainer) getProcessInstance().getProcess()).getDefaultContext( VariableScope.VARIABLE_SCOPE );
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE);
            for (Entry<String, Object> result : results.entrySet()) {
                
                String variableName = result.getKey();
                Variable variable = variableScope.findVariable(variableName);
                if (variable == null) {
                    // check if there is any match for case file data
                    variableName = VariableScope.CASE_FILE_PREFIX + variableName;
                    // check only those that are defined and avoid dynamically created case file variables
                    List<String> definedVariables = Arrays.asList(variableScope.getVariableNames());
                    if (definedVariables.contains(variableName)) {
                        variable = variableScope.findVariable(variableName);
                    }
                }
                
                if (variable != null) {    
                    variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), variableName, result.getValue());    
                    variableScopeInstance.setVariable(variableName, result.getValue());
                }
            }
        }
    }
}
