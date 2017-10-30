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
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.KieInternalServices;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CancelCaseCommand extends CaseCommand<Void> {
    
    private static final long serialVersionUID = 6811181095390934149L;
    private static final Logger logger = LoggerFactory.getLogger(CancelCaseCommand.class);    
    private static CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    
    private String caseId;
    
    private transient ProcessService processService;
    private transient RuntimeDataService runtimeDataService;
    
    private boolean destroy;

    public CancelCaseCommand(IdentityProvider identityProvider, String caseId, ProcessService processService, RuntimeDataService runtimeDataService, boolean destroy) {
        super(identityProvider);
        this.caseId = caseId;
        this.processService = processService;
        this.runtimeDataService = runtimeDataService;
        this.destroy = destroy;
    }

    @Override
    public Void execute(Context context) {
        
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        Collection<ProcessInstanceDesc> caseProcesses = runtimeDataService.getProcessInstancesByCorrelationKey(correlationKey, new QueryContext(0, 1000));
        if (caseProcesses.isEmpty()) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found");
        }
        List<Long> processInstanceIds = caseProcesses.stream()
                .filter(pi -> pi.getState().equals(ProcessInstance.STATE_ACTIVE))
                .sorted((ProcessInstanceDesc o1, ProcessInstanceDesc o2) -> {

                        return Long.valueOf(o2.getParentId()).compareTo(Long.valueOf(o1.getParentId()));
                    }
                )
                .map(pi -> pi.getId()).collect(toList());
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        CaseFileInstance caseFile = getCaseFile(ksession, caseId);  
        
        caseEventSupport.fireBeforeCaseCancelled(caseId, caseFile, processInstanceIds);
        
        logger.debug("Case {} consists of following process instances (ids) {}", caseId, processInstanceIds);
        processService.abortProcessInstances(processInstanceIds);
        caseEventSupport.fireAfterCaseCancelled(caseId, caseFile, processInstanceIds);
        
        if (destroy) {
            RuntimeManager runtimeManager = getRuntimeManager(context);
            if (runtimeManager instanceof PerCaseRuntimeManager) {
                caseEventSupport.fireBeforeCaseDestroyed(caseId, caseFile, processInstanceIds);
                logger.debug("Case {} aborted, destroying case data including per case runtime engine (including working memory)", caseId);
                
                ((PerCaseRuntimeManager) runtimeManager).destroyCase(CaseContext.get(caseId));
                
                caseEventSupport.fireAfterCaseDestroyed(caseId, caseFile, processInstanceIds);
            }
        }
        return null;
    }
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

}
