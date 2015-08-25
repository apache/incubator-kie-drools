/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.test.casemgmt;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.Context;

public class UpdateTimerCommand implements GenericCommand<Object> {

    private static final long serialVersionUID = -8252686458877022330L;
    
    private final long processInstanceId;
    private final String timerName;
    
    private final long delay;
    private final long period;
    private final int repeatLimit;

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay) {
        this(processInstanceId, timerName, delay, 0, 0);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long period, int repeatLimit) {
        this(processInstanceId, timerName, 0, period, repeatLimit);
    }

    public UpdateTimerCommand(long processInstanceId, String timerName, long delay, long period, int repeatLimit) {
        this.processInstanceId = processInstanceId;
        this.timerName = timerName;
        this.delay = delay;
        this.period = period;
        this.repeatLimit = repeatLimit;
    }

    @Override
    public Object execute(Context context) {
        KieSession kieSession = ((KnowledgeCommandContext) context).getKieSession();
        TimerManager tm = getTimerManager(kieSession);

        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId);

        for (NodeInstance nodeInstance : wfp.getNodeInstances()) {
            if (nodeInstance instanceof TimerNodeInstance) {
                TimerNodeInstance tni = (TimerNodeInstance) nodeInstance;
                if (tni.getNodeName().equals(timerName)) {
                    TimerInstance timer = tm.getTimerMap().get(tni.getTimerId());
                    
                    tm.cancelTimer(timer.getTimerId());
                    TimerInstance newTimer = new TimerInstance();
                    
                    if (delay != 0) {
                        long diff = System.currentTimeMillis() - timer.getActivated().getTime();
                        newTimer.setDelay(delay * 1000 - diff);
                    }
                    newTimer.setPeriod(period);
                    newTimer.setRepeatLimit(repeatLimit);
                    newTimer.setTimerId(timer.getTimerId());
                    tm.registerTimer(newTimer, wfp);

                    tni.internalSetTimerId(newTimer.getId());

                    break;
                }
            }
        }
        return null;
    }

    private TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService().getContext()).getKieSession();
        }

        return ((InternalProcessRuntime) ((StatefulKnowledgeSessionImpl) internal).getProcessRuntime()).getTimerManager();
    }

}
