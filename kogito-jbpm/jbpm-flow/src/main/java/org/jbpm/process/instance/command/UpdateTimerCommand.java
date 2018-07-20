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

package org.jbpm.process.instance.command;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "update-timer-command")
@XmlAccessorType(XmlAccessType.NONE)
public class UpdateTimerCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = -8252686458877022330L;
    private static final Logger logger = LoggerFactory.getLogger(UpdateTimerCommand.class);

    @XmlElement
    @XmlSchemaType(name = "long")
    protected long processInstanceId;
    
    @XmlElement
    @XmlSchemaType(name = "long")
    protected long timerId;

    @XmlElement
    @XmlSchemaType(name = "string")
    protected String timerName;

    @XmlElement
    @XmlSchemaType(name = "long")
    protected long delay;

    @XmlElement
    @XmlSchemaType(name = "long")
    protected long period;

    @XmlElement
    @XmlSchemaType(name = "int")
    protected int repeatLimit;

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay) {
        this(processInstanceId, timerName, delay, 0, 0);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long period, int repeatLimit) {
        this(processInstanceId, timerName, 0, period, repeatLimit);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay, long period, int repeatLimit) {
        this.processInstanceId = processInstanceId;
        this.timerName = timerName;
        this.timerId = -1;
        this.delay = delay;
        this.period = period;
        this.repeatLimit = repeatLimit;
    }
    
    public UpdateTimerCommand(long processInstanceId, long timerId, long delay) {
        this(processInstanceId, timerId, delay, 0, 0);
    }

    public UpdateTimerCommand(long processInstanceId, long timerId, long period, int repeatLimit) {
        this(processInstanceId, timerId, 0, period, repeatLimit);
    }

    public UpdateTimerCommand(long processInstanceId, long timerId, long delay, long period, int repeatLimit) {
        this.processInstanceId = processInstanceId;
        this.timerId = timerId;
        this.delay = delay;
        this.period = period;
        this.repeatLimit = repeatLimit;
    }

    @Override
    public Void execute(Context context ) {
        logger.debug("About to cancel timer in process instance {} by name '{}' or id {}", processInstanceId, timerName, timerId);
        KieSession kieSession = ((RegistryContext) context).lookup( KieSession.class );
        TimerManager tm = getTimerManager(kieSession);

        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId);
        if (wfp == null) {
            throw new IllegalArgumentException("Process instance with id " + processInstanceId + " not found");
        }
        for (NodeInstance nodeInstance : wfp.getNodeInstances(true)) {
            if (nodeInstance instanceof TimerNodeInstance) {
                TimerNodeInstance tni = (TimerNodeInstance) nodeInstance;
                if (tni.getNodeName().equals(timerName) || tni.getTimerId() == timerId) {
                    TimerInstance timer = tm.getTimerMap().get(tni.getTimerId());
                    
                    TimerInstance newTimer = rescheduleTimer(timer, tm);
                    logger.debug("New timer {} about to be registered", newTimer);
                    tm.registerTimer(newTimer, wfp);                    
                    tni.internalSetTimerId(newTimer.getId());
                    logger.debug("New timer {} successfully registered", newTimer);

                    break;
                }
            } else if (nodeInstance instanceof StateBasedNodeInstance) {
                StateBasedNodeInstance sbni = (StateBasedNodeInstance) nodeInstance;
                List<Long> timerList = sbni.getTimerInstances();
                if (sbni.getNodeName().equals(timerName) || (timerList != null && timerList.contains(timerId))) {
                    
                    if (timerList != null && timerList.size() == 1) {
                        TimerInstance timer = tm.getTimerMap().get(timerList.get(0));
    
                        TimerInstance newTimer = rescheduleTimer(timer, tm);
                        logger.debug("New timer {} about to be registered", newTimer);
                        tm.registerTimer(newTimer, wfp);                        
                        timerList.clear();
                        timerList.add(newTimer.getId());
    
                        sbni.internalSetTimerInstances(timerList);
                        logger.debug("New timer {} successfully registered", newTimer);
                    
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public void setProcessInstanceId(Long procInstId) {
        this.processInstanceId = procInstId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    protected TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession();
        }

        return ((InternalProcessRuntime) ((StatefulKnowledgeSessionImpl) internal).getProcessRuntime()).getTimerManager();
    }

    public String toString() {
        return "processInstance.updateTimer(" + timerName + ", " + delay + ", " + period + ", " + repeatLimit + ");";
    }

    protected long calculateDelay(long delay, TimerInstance timer) {
        long diff = System.currentTimeMillis() - timer.getActivated().getTime();
        return delay * 1000 - diff;
    }
    
    protected TimerInstance rescheduleTimer(TimerInstance timer, TimerManager tm) {
        logger.debug("Found timer {} that is going to be canceled", timer);
        tm.cancelTimer(timer.getTimerId());
        logger.debug("Timer {} canceled successfully", timer);
        
        TimerInstance newTimer = new TimerInstance();

        if (delay != 0) {
            newTimer.setDelay(calculateDelay(delay, timer));
        }
        newTimer.setPeriod(period);
        newTimer.setRepeatLimit(repeatLimit);
        newTimer.setTimerId(timer.getTimerId());        
        
        return newTimer;
    }
}
