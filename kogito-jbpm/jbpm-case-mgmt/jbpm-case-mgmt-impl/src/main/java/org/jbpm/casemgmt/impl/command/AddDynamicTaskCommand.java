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
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.identity.IdentityProvider;
import org.kie.api.runtime.Context;

import java.util.Map;

/**
 * Adds task to selected ad hoc process instance with given parameters
 */
public class AddDynamicTaskCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335953L;

    private String caseId;
    private String nodeType;
    private long processInstanceId;
    private Map<String, Object> parameters;
    
    public AddDynamicTaskCommand(IdentityProvider identityProvider, String caseId, String nodeType, Long processInstanceId, Map<String, Object> parameters) {
        super(identityProvider);
        this.caseId = caseId;
        this.nodeType = nodeType;
        this.processInstanceId = processInstanceId;
        this.parameters = parameters;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId);
        }        
        
        CaseFileInstance caseFile = getCaseFile(ksession, caseId);  
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeDynamicTaskAdded(caseId, caseFile, processInstanceId, nodeType, parameters);
        
        DynamicUtils.addDynamicWorkItem(processInstance, ksession, nodeType, parameters);
        
        caseEventSupport.fireAfterDynamicTaskAdded(caseId, caseFile, processInstanceId, nodeType, parameters);
        return null;
    }

}
