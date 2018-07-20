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

import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.command.impl.RegistryContext;
import org.jbpm.kie.services.impl.admin.ProcessNodeImpl;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.admin.ProcessNode;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.ProcessInstanceIdCommand;

public class ListNodesCommand implements ExecutableCommand<List<ProcessNode>>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = -8252686458877022330L;

    private long processInstanceId;

    public ListNodesCommand(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<ProcessNode> execute(Context context) {
    	List<ProcessNode> nodes = null;
    	
    	KieSession kieSession = ((RegistryContext) context).lookup( KieSession.class );
        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId, true);
        
        if (wfp == null) {
        	throw new ProcessInstanceNotFoundException("No process instance can be found for id " + processInstanceId);
        }

        String processId = wfp.getProcessId();
        nodes = wfp.getRuleFlowProcess().getNodesRecursively().stream()
                                            .map(n -> new ProcessNodeImpl(n.getName(), n.getId(), n.getClass().getSimpleName(), processId))
                                            .collect(Collectors.toList());
        
        return nodes;
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
