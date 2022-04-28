/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.internal.process.event.KogitoEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobId;
import org.kie.kogito.jobs.JobIdResolver;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.TimerJobId;
import org.kie.kogito.timer.TimerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;

public abstract class StateBasedNodeInstance extends ExtendedNodeInstanceImpl implements EventBasedNodeInstanceInterface, KogitoEventListener {

    private static final long serialVersionUID = 510l;

    private static final Logger logger = LoggerFactory.getLogger(StateBasedNodeInstance.class);

    private List<String> timerInstances;

    public StateBasedNode getEventBasedNode() {
        return (StateBasedNode) getNode();
    }

    @Override
    public void internalTrigger(KogitoNodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
            return;
        }
        // activate timers
        Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
        if (timers != null) {
            addTimerListener();
            timerInstances = new ArrayList<>(timers.size());
            JobsService jobService = ((KogitoProcessRuntime.Provider) getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getKogitoProcessRuntime().getJobsService();
            for (Timer timer : timers.keySet()) {
                ProcessInstanceJobDescription jobDescription =
                        ProcessInstanceJobDescription.of(new TimerJobId(timer.getId()),
                                createTimerInstance(timer),
                                getProcessInstance().getStringId(),
                                getProcessInstance().getRootProcessInstanceId(),
                                getProcessInstance().getProcessId(),
                                getProcessInstance().getRootProcessId(),
                                Optional.ofNullable(from).map(KogitoNodeInstance::getStringId).orElse(null));
                timerInstances.add(jobService.scheduleProcessInstanceJob(jobDescription));
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
                this.slaCompliance = KogitoProcessInstance.SLA_PENDING;
                logger.debug("SLA for node instance {} is PENDING with due date {}", this.getStringId(), this.slaDueDate);
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

                    String tempDelay = resolveExpression(timer.getDelay());
                    String tempPeriod = resolveExpression(timer.getPeriod());
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

                                return DurationExpirationTime.repeat(businessCalendar.calculateBusinessTimeAsDuration(tempDelay), businessCalendar.calculateBusinessTimeAsDuration(tempPeriod),
                                        repeatLimit);
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

                case Timer.TIME_DURATION:
                    delay = resolveExpression(timer.getDelay());

                    return DurationExpirationTime.repeat(businessCalendar.calculateBusinessTimeAsDuration(delay));
                case Timer.TIME_DATE:
                    // even though calendar is available concrete date was provided so it shall be used
                    return ExactExpirationTime.of(timer.getDate());

                default:
                    throw new UnsupportedOperationException("Not supported timer definition");
            }
        } else {
            return configureTimerInstance(timer);
        }
    }

    protected ExpirationTime configureTimerInstance(Timer timer) {
        String s = null;
        long duration = -1;
        switch (timer.getTimeType()) {
            case Timer.TIME_CYCLE:
                if (timer.getPeriod() != null) {

                    long actualDelay = DateTimeUtils.parseDuration(resolveExpression(timer.getDelay()));
                    if (timer.getPeriod() == null) {
                        return DurationExpirationTime.repeat(actualDelay, actualDelay, Integer.MAX_VALUE);
                    } else {
                        return DurationExpirationTime.repeat(actualDelay, DateTimeUtils.parseDuration(resolveExpression(timer.getPeriod())), Integer.MAX_VALUE);
                    }
                } else {
                    String resolvedDelay = resolveExpression(timer.getDelay());

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
                        return DurationExpirationTime.repeat(repeatValues[0], repeatValues[0], Integer.MAX_VALUE);
                    }
                }

            case Timer.TIME_DURATION:

                try {
                    duration = DateTimeUtils.parseDuration(timer.getDelay());
                } catch (RuntimeException e) {
                    // cannot parse delay, trying to interpret it
                    s = resolveExpression(timer.getDelay());
                    duration = DateTimeUtils.parseDuration(s);
                }
                return DurationExpirationTime.after(duration);

            case Timer.TIME_DATE:
                try {
                    return ExactExpirationTime.of(timer.getDate());
                } catch (RuntimeException e) {
                    // cannot parse delay, trying to interpret it
                    s = resolveExpression(timer.getDate());
                    return ExactExpirationTime.of(s);
                }
        }
        throw new UnsupportedOperationException("Not supported timer definition");
    }

    protected void handleSLAViolation() {
        if (slaCompliance == KogitoProcessInstance.SLA_PENDING) {
            InternalProcessRuntime processRuntime = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime());
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
            logger.debug("SLA violated on node instance {}", getStringId());
            this.slaCompliance = KogitoProcessInstance.SLA_VIOLATED;
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
        } else if (("slaViolation:" + getStringId()).equals(type)) {
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
                if (timerInstance.getRepeatLimit() == 0) {
                    timerInstances.remove(timerInstance.getId());
                }
                executeAction((Action) entry.getValue().getMetaData("Action"));
                return;
            }
        }
    }

    @Override
    public String[] getEventTypes() {
        return new String[] { "timerTriggered", getActivationType() };
    }

    public void triggerCompleted() {
        triggerCompleted(CONNECTION_DEFAULT_TYPE, true);
    }

    @Override
    public void addEventListeners() {
        if (timerInstances != null && (!timerInstances.isEmpty()) || (this.slaTimerId != null && !this.slaTimerId.trim().isEmpty())) {
            addTimerListener();
        }
        if (slaCompliance == KogitoProcessInstance.SLA_PENDING) {
            getProcessInstance().addEventListener("slaViolation:" + getStringId(), this, true);
        }
    }

    protected void addTimerListener() {
        getProcessInstance().addEventListener("timerTriggered", this, false);
        getProcessInstance().addEventListener("timer", this, true);
        getProcessInstance().addEventListener("slaViolation:" + getStringId(), this, true);
    }

    @Override
    public void removeEventListeners() {
        getProcessInstance().removeEventListener("timerTriggered", this, false);
        getProcessInstance().removeEventListener("timer", this, true);
        getProcessInstance().removeEventListener("slaViolation:" + getStringId(), this, true);
    }

    @Override
    public void triggerCompleted(String type, boolean remove) {
        if (this.slaCompliance == KogitoProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the node instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = KogitoProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = KogitoProcessInstance.STATE_COMPLETED;
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
        if (this.slaCompliance == KogitoProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the process instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = KogitoProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = KogitoProcessInstance.SLA_ABORTED;
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
            JobsService jobService = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getJobsService();
            for (String id : timerInstances) {
                jobService.cancelJob(id);
            }
        }
    }

    private void cancelSlaTimer() {
        if (this.slaTimerId != null && !this.slaTimerId.trim().isEmpty()) {
            JobsService jobService = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getJobsService();
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
        for (Iterator<?> it = declarations.values().iterator(); it.hasNext();) {
            Declaration declaration = (Declaration) it.next();
            if ("processInstance".equals(declaration.getIdentifier())
                    || "org.kie.api.runtime.process.WorkflowProcessInstance".equals(declaration.getTypeName())) {
                Object value = declaration.getValue(
                        ((ReteEvaluator) getProcessInstance().getKnowledgeRuntime()),
                        activation.getTuple().get(declaration).getObject());
                if (value instanceof ProcessInstance) {
                    return ((ProcessInstance) value).getStringId().equals(getProcessInstance().getStringId());
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

    public Map<String, String> extractTimerEventInformation() {
        if (getTimerInstances() != null) {
            for (String id : getTimerInstances()) {
                JobId jobId = JobIdResolver.resolve(id).decode(id);

                for (Timer entry : getEventBasedNode().getTimers().keySet()) {
                    if (Objects.equals(entry.getId(), jobId.correlationId())) {
                        Map<String, String> properties = new HashMap<>();
                        properties.put("TimerID", id);
                        properties.put("Delay", entry.getDelay());
                        properties.put("Period", entry.getPeriod());
                        properties.put("Date", entry.getDate());

                        return properties;
                    }
                }
            }
        }

        return null;
    }

    protected final KogitoProcessContext getProcessContext(Throwable e) {
        KogitoProcessContext context = ContextFactory.fromNode(this);
        context.getContextData().put("Exception", e);
        return context;
    }
}
