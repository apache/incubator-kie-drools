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

package org.jbpm.casemgmt.impl.command;

import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.api.StageNotFoundException;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.identity.IdentityProvider;

/**
 * Triggers a ad hoc activity within given stage (adhoc subprocess)
 *
 */
public class TriggerAdHocNodeInStageCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335954L;

    private String fragmentName;
    private String stageId;
    private long processInstanceId;
    private Object data;
    
    public TriggerAdHocNodeInStageCommand(IdentityProvider identityProvider, Long processInstanceId, String stageId, String fragmentName, Object data) {
        super(identityProvider);
        this.fragmentName = fragmentName;
        this.processInstanceId = processInstanceId;
        this.stageId = stageId;
        this.data = data;
        
        if (processInstanceId == null || stageId == null) {
            throw new IllegalArgumentException("Process instance id and stage id are mandatory");
        }
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        
        DynamicNodeInstance dynamicContext = (DynamicNodeInstance) ((WorkflowProcessInstanceImpl) processInstance).getNodeInstances(true).stream()
                .filter(ni -> (ni instanceof DynamicNodeInstance) && stageId.equals(ni.getNode().getMetaData().get("UniqueId")))
                .findFirst()
                .orElseThrow(() -> new StageNotFoundException("No stage found with id " + stageId));

        
                
        dynamicContext.signalEvent(fragmentName, data);                
        return null;
    }

}
