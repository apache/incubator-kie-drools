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

import org.drools.core.command.impl.RegistryContext;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.services.api.NodeNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerNodeCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = -8252686458877022331L;
    private static final Logger logger = LoggerFactory.getLogger(TriggerNodeCommand.class);

    private long processInstanceId;
    private long nodeId;

    public TriggerNodeCommand(long processInstanceId, long nodeId) {
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
    }

    public Void execute(Context context) {
    	KieSession kieSession = ((RegistryContext) context).lookup( KieSession.class );
    	logger.debug("About to trigger (create) node instance for node {} in process instance {}", nodeId, processInstanceId); 
        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId, false);
        if (wfp == null) {
            throw new ProcessInstanceNotFoundException("Process instance with id " + processInstanceId + " not found");
        }
        
        Node node = wfp.getRuleFlowProcess().getNodesRecursively().stream().filter(ni -> ni.getId() == nodeId).findFirst().orElse(null);
        if (node == null) {
            throw new NodeNotFoundException("Node instance with id " + nodeId + " not found");
        }
        
        logger.debug("Triggering node {} on process instance {}", node, wfp);
        wfp.getNodeInstance(node).trigger(null, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        logger.debug("Node {} successfully triggered", node);
        
        return null;
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
