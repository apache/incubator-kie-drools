/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.services.api.ProcessService;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KieInternalServices;
import org.kie.internal.command.Context;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReopenCaseCommand extends CaseCommand<Void> {
    
    private static final long serialVersionUID = 6811181095390934146L;

    private static final Logger logger = LoggerFactory.getLogger(ReopenCaseCommand.class);
    
    private static CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    
    private String caseId;
    private String deploymentId;
    private String caseDefinitionId;
    private Map<String, Object> data;
    
    private transient ProcessService processService;

    public ReopenCaseCommand(String caseId, String deploymentId, String caseDefinitionId, Map<String, Object> data, ProcessService processService) {
        this.caseId = caseId;
        this.deploymentId = deploymentId;
        this.caseDefinitionId = caseDefinitionId;
        this.data = data;
        this.processService = processService;
    }

    @Override
    public Void execute(Context context) {
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeCaseReopened(caseId, deploymentId, caseDefinitionId, data);
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        
        if (data != null && !data.isEmpty()) {
            logger.debug("Updating case file in working memory");
            Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
            if (caseFiles.size() == 0) {
                throw new CaseNotFoundException("Case with id " + caseId + " was not found");
            }
            CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();                        
            FactHandle factHandle = ksession.getFactHandle(caseFile);
            
            caseFile.addAll(data);
            
            ksession.update(factHandle, caseFile);
        }
        logger.debug("Starting process instance for case {} and case definition {}", caseId, caseDefinitionId);
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        Map<String, Object> params = new HashMap<>();
        // set case id to allow it to use CaseContext when creating runtime engine
        params.put(EnvironmentName.CASE_ID, caseId);
        long processInstanceId = processService.startProcess(deploymentId, caseDefinitionId, correlationKey, params);
        logger.debug("Case {} successfully reopened (process instance id {})", caseId, processInstanceId);
        caseEventSupport.fireAfterCaseReopened(caseId, deploymentId, caseDefinitionId, data, processInstanceId);
        return null;
    }
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

}
