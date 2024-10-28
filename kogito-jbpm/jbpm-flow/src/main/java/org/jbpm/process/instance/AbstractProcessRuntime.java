/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.instance;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.time.TimeUtils;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.event.KogitoProcessEventListenerAdapter;
import org.jbpm.process.instance.event.KogitoProcessEventSupportImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Process;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.Application;
import org.kie.kogito.calendar.BusinessCalendar;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.signal.SignalManager;

import static org.jbpm.process.core.constants.CalendarConstants.BUSINESS_CALENDAR_ENVIRONMENT_KEY;

public abstract class AbstractProcessRuntime implements InternalProcessRuntime {

    protected KogitoProcessEventSupport processEventSupport;
    protected KogitoProcessRuntimeImpl kogitoProcessRuntime = new KogitoProcessRuntimeImpl(this);

    protected SignalManager signalManager;
    protected JobsService jobService;

    private final Application application;

    private final Map<ProcessEventListener, KogitoProcessEventListener> listenersMap = new IdentityHashMap<>();

    protected AbstractProcessRuntime(Application application) {
        this.application = application;
    }

    @Override
    public KogitoProcessRuntime getKogitoProcessRuntime() {
        return kogitoProcessRuntime;
    }

    @Override
    public KogitoProcessEventSupport getProcessEventSupport() {
        return processEventSupport;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void addEventListener(final ProcessEventListener listener) {
        ((KogitoProcessEventSupportImpl) this.processEventSupport).addEventListener(asKogitoProcessEventListener(listener));
    }

    @Override
    public void removeEventListener(final ProcessEventListener listener) {
        ((KogitoProcessEventSupportImpl) this.processEventSupport).removeEventListener(removeKogitoProcessEventListener(listener));
    }

    @Override
    public List<ProcessEventListener> getProcessEventListeners() {
        return (List<ProcessEventListener>) (Object) ((KogitoProcessEventSupportImpl) this.processEventSupport).getEventListeners();
    }

    protected void initStartTimers(Collection<Process> processes, InternalKnowledgeRuntime kruntime) {
        for (Process process : processes) {
            RuleFlowProcess p = (RuleFlowProcess) process;
            List<StartNode> startNodes = p.getTimerStart();
            if (startNodes != null && !startNodes.isEmpty()) {
                for (StartNode startNode : startNodes) {
                    if (startNode != null && startNode.getTimer() != null) {
                        jobService.scheduleProcessJob(ProcessJobDescription.of(createTimerInstance(startNode.getTimer(), kruntime), p.getId()));
                    }
                }
            }
        }
    }

    private KogitoProcessEventListener asKogitoProcessEventListener(ProcessEventListener processEventListener) {
        if (processEventListener instanceof KogitoProcessEventListener) {
            return ((KogitoProcessEventListener) processEventListener);
        }
        return listenersMap.computeIfAbsent(processEventListener, KogitoProcessEventListenerAdapter::new);
    }

    private KogitoProcessEventListener removeKogitoProcessEventListener(ProcessEventListener processEventListener) {
        if (processEventListener instanceof KogitoProcessEventListener) {
            return ((KogitoProcessEventListener) processEventListener);
        }
        return listenersMap.remove(processEventListener);
    }

    protected ExpirationTime createTimerInstance(Timer timer, InternalKnowledgeRuntime kruntime) {

        if (kruntime != null && kruntime.getEnvironment().get(BUSINESS_CALENDAR_ENVIRONMENT_KEY) != null) {
            BusinessCalendar businessCalendar = (BusinessCalendar) kruntime.getEnvironment().get(BUSINESS_CALENDAR_ENVIRONMENT_KEY);

            long delay = businessCalendar.calculateBusinessTimeAsDuration(timer.getDelay());

            if (timer.getPeriod() == null) {
                return DurationExpirationTime.repeat(delay);
            } else {
                long period = businessCalendar.calculateBusinessTimeAsDuration(timer.getPeriod());

                return DurationExpirationTime.repeat(delay, period);
            }
        } else {
            return configureTimerInstance(timer);
        }
    }

    private ExpirationTime configureTimerInstance(Timer timer) {
        long duration = -1;
        switch (timer.getTimeType()) {
            case Timer.TIME_CYCLE:
                // when using ISO date/time period is not set
                long[] repeatValues = DateTimeUtils.parseRepeatableDateTime(timer.getDelay());
                if (repeatValues.length == 3) {
                    return DurationExpirationTime.repeat(repeatValues[1], repeatValues[2]);
                } else {
                    long delay = repeatValues[0];
                    long period = -1;
                    try {
                        period = TimeUtils.parseTimeString(timer.getPeriod());

                    } catch (RuntimeException e) {
                        period = repeatValues[0];
                    }

                    return DurationExpirationTime.repeat(delay, period);
                }

            case Timer.TIME_DURATION:

                duration = DateTimeUtils.parseDuration(timer.getDelay());
                return DurationExpirationTime.repeat(duration);

            case Timer.TIME_DATE:

                return ExactExpirationTime.of(timer.getDate());

            default:
                throw new UnsupportedOperationException("Not supported timer definition");
        }

    }
}
