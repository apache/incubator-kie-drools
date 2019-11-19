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
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
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
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.services.time.TimerInstance;
import org.kie.services.time.impl.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StateBasedNodeInstance extends ExtendedNodeInstanceImpl implements EventBasedNodeInstanceInterface,
                                                                                         EventListener {

    private static final long serialVersionUID = 510l;

    private static final Logger logger = LoggerFactory.getLogger(StateBasedNodeInstance.class);

    private List<String> timerInstances;

    public StateBasedNode getEventBasedNode() {
        return (StateBasedNode) getNode();
    }

    @Override
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
            timerInstances = new ArrayList<>(timers.size());
            JobsService jobService = ((InternalProcessRuntime)
                    getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getJobsService();
            for (Timer timer : timers.keySet()) {
                ExpirationTime expirationTime = createTimerInstance(timer);
                String jobId = jobService.scheduleProcessInstanceJob(ProcessInstanceJobDescription.of(timer.getId(), expirationTime, getProcessInstance().getId(), getProcessInstance().getRootProcessInstanceId(), getProcessInstance().getProcessId(), getProcessInstance().getRootProcessId()));
                timerInstances.add(jobId);
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

    @Override
    protected void configureSla() {
        String slaDueDateExpression = (String) getNode().getMetaData().get("customSLADueDate");
        if (slaDueDateExpression != null) {
            TimerInstance timer = ((WorkflowProcessInstanceImpl) getProcessInstance()).configureSLATimer(slaDueDateExpression);
            if (timer != null) {
                this.slaTimerId = timer.getId();
                this.slaDueDate = new Date(System.currentTimeMillis() + timer.getDelay());
                this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.SLA_PENDING;
                logger.debug("SLA for node instance {} is PENDING with due date {}", this.getId(), this.slaDueDate);
                addTimerListener();
            }
        }
    }

    protected ExpirationTime createTimerInstance(Timer timer) {
        
        KieRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (kruntime != null && kruntime.getEnvironment().get("jbpm.business.calendar") != null) {
            BusinessCalendar businessCalendar = (BusinessCalendar) kruntime.getEnvironment().get("jbpm.business.calendar");
            String delay = null;
            switch (timer.getTimeType()) {
                case Timer.TIME_CYCLE:

                    if (CronExpression.isValidExpression(timer.getDelay())) {
                        //timerInstance.setCronExpression(timer.getDelay());
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
                                    if (repeatLimit <= -1) {
                                        repeatLimit = Integer.MAX_VALUE;
                                    }
                                    
                                    return DurationExpirationTime.repeat(businessCalendar.calculateBusinessTimeAsDuration(tempDelay), businessCalendar.calculateBusinessTimeAsDuration(tempPeriod), repeatLimit);
                                } catch (NumberFormatException e) {
                                    // ignore
                                }
                            }
                            
                        }
                        long actualDelay = businessCalendar.calculateBusinessTimeAsDuration(tempDelay);
                        if (tempPeriod == null) {
                            return DurationExpirationTime.repeat(actualDelay, actualDelay, Integer.MAX_VALUE);
                        } else {
                            return DurationExpirationTime.repeat(actualDelay, businessCalendar.calculateBusinessTimeAsDuration(tempPeriod), Integer.MAX_VALUE);
                        }
                    }
                    break;
                case Timer.TIME_DURATION:
                    delay = resolveVariable(timer.getDelay());
                    
                    return DurationExpirationTime.repeat(businessCalendar.calculateBusinessTimeAsDuration(delay));
                case Timer.TIME_DATE:
                    // even though calendar is available concrete date was provided so it shall be used
                    return ExactExpirationTime.of(timer.getDate());               
            }
        } else {
            return configureTimerInstance(timer);
        }        
        
        throw new UnsupportedOperationException("Not supported timer definition");
    }

    protected ExpirationTime configureTimerInstance(Timer timer) {
        String s = null;
        long duration = -1;
        switch (timer.getTimeType()) {
            case Timer.TIME_CYCLE:
                if (timer.getPeriod() != null) {
                    
                    long actualDelay = DateTimeUtils.parseDuration(resolveVariable(timer.getDelay()));
                    if (timer.getPeriod() == null) {
                        return DurationExpirationTime.repeat(actualDelay, actualDelay, Integer.MAX_VALUE);
                    } else {
                        return DurationExpirationTime.repeat(actualDelay, DateTimeUtils.parseDuration(resolveVariable(timer.getPeriod())), Integer.MAX_VALUE);
                    }
                } else {
                    String resolvedDelay = resolveVariable(timer.getDelay());
                    if (CronExpression.isValidExpression(resolvedDelay)) {
                        //timerInstance.setCronExpression(resolvedDelay);
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
                            int parsedReapedCount = (int) repeatValues[0];
                            if (parsedReapedCount <= -1) {
                                parsedReapedCount = Integer.MAX_VALUE;
                            }
                            
                            return DurationExpirationTime.repeat(repeatValues[1], repeatValues[2], parsedReapedCount);
                        } else if (repeatValues.length == 2) {
                            return DurationExpirationTime.repeat(repeatValues[0], repeatValues[1], Integer.MAX_VALUE);
                        } else {
                            return DurationExpirationTime.repeat(repeatValues[0],repeatValues[0], Integer.MAX_VALUE);
                        }
                    }
                }

            case Timer.TIME_DURATION:

                try {
                    duration = DateTimeUtils.parseDuration(timer.getDelay());
                } catch (RuntimeException e) {
                    // cannot parse delay, trying to interpret it
                    s = resolveVariable(timer.getDelay());
                    duration = DateTimeUtils.parseDuration(s);
                }                
                return DurationExpirationTime.after(duration);

            case Timer.TIME_DATE:
                try {
                    duration = DateTimeUtils.parseDateAsDuration(timer.getDate());
                    return ExactExpirationTime.of(timer.getDate());
                } catch (RuntimeException e) {
                    // cannot parse delay, trying to interpret it
                    s = resolveVariable(timer.getDate());
                    return ExactExpirationTime.of(s);
                }                
        }
        throw new UnsupportedOperationException("Not supported timer definition");

    }

    protected String resolveVariable(String s) {
        if (s == null) {
            return null;
        }
        // cannot parse delay, trying to interpret it
        Map<String, String> replacements = new HashMap<>();
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
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }

        return s;
    }

    protected void handleSLAViolation() {
        if (slaCompliance == org.kie.api.runtime.process.ProcessInstance.SLA_PENDING) {
            InternalProcessRuntime processRuntime = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime());
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
            logger.debug("SLA violated on node instance {}", getId());
            this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.SLA_VIOLATED;
            this.slaTimerId = null;
            processRuntime.getProcessEventSupport().fireAfterSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
        }
    }

    @Override
    public void signalEvent(String type, Object event) {
        if ("timerTriggered".equals(type)) {
            TimerInstance timerInstance = (TimerInstance) event;
            if (timerInstances != null && timerInstances.contains(timerInstance.getId())) {
                triggerTimer(timerInstance);
            } else if (timerInstance.getId().equals(slaTimerId)) {
                handleSLAViolation();
            }
        } else if (("slaViolation:" + getId()).equals(type)) {

            handleSLAViolation();
        } else if (type.equals(getActivationType()) && event instanceof MatchCreatedEvent) {
            String name = ((MatchCreatedEvent) event).getMatch().getRule().getName();
            if (checkProcessInstance((Activation) ((MatchCreatedEvent) event).getMatch())) {
                ((MatchCreatedEvent) event).getKieRuntime().signalEvent(name, null);
            }
        }
    }

    private void triggerTimer(TimerInstance timerInstance) {
        for (Map.Entry<Timer, DroolsAction> entry : getEventBasedNode().getTimers().entrySet()) {
            if (entry.getKey().getId() == timerInstance.getTimerId()) {
                executeAction((Action) entry.getValue().getMetaData("Action"));
                return;
            }
        }
    }

    @Override
    public String[] getEventTypes() {
        return new String[]{"timerTriggered", getActivationType()};
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }

    @Override
    public void addEventListeners() {
        if (timerInstances != null && (!timerInstances.isEmpty()) || (this.slaTimerId != null && !this.slaTimerId.trim().isEmpty())) {
            addTimerListener();
        }
        if (slaCompliance == org.kie.api.runtime.process.ProcessInstance.SLA_PENDING) {
            getProcessInstance().addEventListener("slaViolation:" + getId(), this, true);
        }
    }

    protected void addTimerListener() {
        getProcessInstance().addEventListener("timerTriggered", this, false);
        getProcessInstance().addEventListener("timer", this, true);
        getProcessInstance().addEventListener("slaViolation:" + getId(), this, true);
    }

    @Override
    public void removeEventListeners() {
        getProcessInstance().removeEventListener("timerTriggered", this, false);
        getProcessInstance().removeEventListener("timer", this, true);
        getProcessInstance().removeEventListener("slaViolation:" + getId(), this, true);
    }

    @Override
    public void triggerCompleted(String type, boolean remove) {
        if (this.slaCompliance == org.kie.api.runtime.process.ProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the node instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
            }
        }
        cancelSlaTimer();
        ((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer()).setCurrentLevel(getLevel());
        cancelTimers();
        removeActivationListener();
        super.triggerCompleted(type, remove);
    }

    public List<String> getTimerInstances() {
        return timerInstances;
    }

    public void internalSetTimerInstances(List<String> timerInstances) {
        this.timerInstances = timerInstances;
    }

    @Override
    public void cancel() {
        if (this.slaCompliance == org.kie.api.runtime.process.ProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the process instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = org.kie.api.runtime.process.ProcessInstance.SLA_ABORTED;
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
            JobsService jobService = ((InternalProcessRuntime)
                    getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getJobsService();
            for (String id : timerInstances) {
                jobService.cancelJob(id);
            }
        }
    }

    private void cancelSlaTimer() {
        if (this.slaTimerId != null && !this.slaTimerId.trim().isEmpty()) {
            JobsService jobService = ((InternalProcessRuntime)
                    getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getJobsService();
            jobService.cancelJob(this.slaTimerId);
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
        for (Iterator<?> it = declarations.values().iterator(); it.hasNext(); ) {
            Declaration declaration = (Declaration) it.next();
            if ("processInstance".equals(declaration.getIdentifier())
                    || "org.kie.api.runtime.process.WorkflowProcessInstance".equals(declaration.getTypeName())) {
                Object value = declaration.getValue(
                        ((StatefulKnowledgeSessionImpl) getProcessInstance().getKnowledgeRuntime()).getInternalWorkingMemory(),
                        activation.getTuple().get(declaration).getObject());
                if (value instanceof ProcessInstance) {
                    return ((ProcessInstance) value).getId().equals(getProcessInstance().getId());
                }
            }
        }
        return true;
    }

    protected boolean checkDeclarationMatch(Match match, String matchVariable) {
        if (matchVariable == null) {
            // no extra check is needed
            return true;
        }

        Object dec = match.getDeclarationIds().contains("$" + matchVariable) ? match.getDeclarationValue("$" + matchVariable) : match.getDeclarationValue(matchVariable);
        Object var = getVariable(matchVariable);

        return var.equals(dec);
    }

    protected void mapDynamicOutputData(Map<String, Object> results) {
        if (results != null && !results.isEmpty()) {
            VariableScope variableScope = (VariableScope) ((ContextContainer) getProcessInstance().getProcess()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE);
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
