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

package org.jbpm.kie.services.impl.admin.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.kie.services.impl.admin.TimerInstanceImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.admin.TimerInstance;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;

public class ListTimersCommand implements ExecutableCommand<List<TimerInstance>>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = -8252686458877022330L;

    private long processInstanceId;

    public ListTimersCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<TimerInstance> execute(Context context ) {
    	List<TimerInstance> timers = new ArrayList<TimerInstance>();
    	
    	KieSession kieSession = ((RegistryContext) context).lookup( KieSession.class );
        TimerManager tm = getTimerManager(kieSession);

        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId, true);
        
        if (wfp == null) {
        	throw new ProcessInstanceNotFoundException("No process instance can be found for id " + processInstanceId);
        }

        processNodeInstance(tm, wfp, timers);
        
        return timers;
    }

    private TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession();
        }

        return ((InternalProcessRuntime) ((StatefulKnowledgeSessionImpl) internal).getProcessRuntime()).getTimerManager();
    }
    
    private TimerInstanceImpl buildTimer(org.jbpm.process.instance.timer.TimerInstance timerInstance) {
        TimerInstanceImpl timer = new TimerInstanceImpl();
    	
    	if (timerInstance != null) {
	    	timer.setActivationTime(timerInstance.getActivated());
	    	timer.setLastFireTime(timerInstance.getLastTriggered());
	    	timer.setNextFireTime(new Date(timerInstance.getActivated().getTime() + timerInstance.getDelay()));
	    	timer.setDelay(timerInstance.getDelay());
	    	timer.setPeriod(timerInstance.getPeriod());
	    	timer.setRepeatLimit(timerInstance.getRepeatLimit());
	    	timer.setTimerId(timerInstance.getId());
	    	timer.setProcessInstanceId(timerInstance.getProcessInstanceId());
	    	timer.setSessionId(timerInstance.getSessionId());
    	}
    	
    	return timer;
    }
    
    protected void processNodeInstance(TimerManager tm, NodeInstanceContainer container, List<TimerInstance> timers) {
    	for (NodeInstance nodeInstance : container.getNodeInstances()) {
            if (nodeInstance instanceof TimerNodeInstance) {
                TimerNodeInstance tni = (TimerNodeInstance) nodeInstance;
            	org.jbpm.process.instance.timer.TimerInstance timer = tm.getTimerMap().get(tni.getTimerId());
            	
            	TimerInstanceImpl details = buildTimer(timer);
                details.setTimerName(resolveVariable(tni.getNodeName(), tni));
                
                timers.add(details);
            
            } else if (nodeInstance instanceof StateBasedNodeInstance) {
                StateBasedNodeInstance sbni = (StateBasedNodeInstance) nodeInstance;
                                
            	List<Long> timerList = sbni.getTimerInstances();
            	if (timerList != null) {
                    for (Long timerId : timerList) {
                        org.jbpm.process.instance.timer.TimerInstance timer = tm.getTimerMap().get(timerId);

                        TimerInstanceImpl details = buildTimer(timer);
                        details.setTimerName(resolveVariable(sbni.getNodeName(), sbni));
                        
                        timers.add(details);
                    }
            	}
                
            }
            
            if (nodeInstance instanceof NodeInstanceContainer) {
            	processNodeInstance(tm, (NodeInstanceContainer) nodeInstance, timers);
            }
        }
    }

    protected String resolveVariable(String s, NodeInstance pi) {
        if (s == null) {
            return null;
        }
        // cannot parse delay, trying to interpret it
        Map<String, String> replacements = new HashMap<String, String>();
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {

                Object variableValue = pi.getVariable(paramName);
                String variableValueString = variableValue == null ? "" : variableValue.toString();
                replacements.put(paramName, variableValueString);

            }
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }

        return s;
    }

    @Override
    public void setProcessInstanceId(Long procInstId) {
        this.processInstanceId = procInstId;
        
    }

    @Override
    public Long getProcessInstanceId() {
        return this.processInstanceId;
    }
}
