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

package org.jbpm.casemgmt.impl.wih;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.event.CaseCancelEvent;
import org.jbpm.casemgmt.api.event.CaseCloseEvent;
import org.jbpm.casemgmt.api.event.CaseDestroyEvent;
import org.jbpm.casemgmt.api.event.CaseEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NotifyParentCaseEventListener extends DefaultProcessEventListener implements CaseEventListener, Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(NotifyParentCaseEventListener.class);
    
    private IdentityProvider identityProvider;
    
    
    public NotifyParentCaseEventListener(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }
    
    @Override
    public void afterCaseClosed(CaseCloseEvent event) {
        notifyParentOnCompletion(event);
    }

    @Override
    public void afterCaseCancelled(CaseCancelEvent event) {
        notifyParentOnCompletion(event);
    }

    @Override
    public void afterCaseDestroyed(CaseDestroyEvent event) {
        notifyParentOnCompletion(event);
    }
    
    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        
        CaseFileInstance caseFile = getCaseFile((KieSession) event.getKieRuntime());
        if (caseFile != null) {
            String caseId = ((WorkflowProcessInstanceImpl) event.getProcessInstance()).getCorrelationKey();
            if (caseFile.getCaseId().equals(caseId)) {
                logger.debug("Process instance {} that represents main case instance {} has completed/was aborted, notify parent if exists",
                             event.getProcessInstance().getId(), caseId);
                CaseEvent caseEvent = null;
                if (event.getProcessInstance().getState() == ProcessInstance.STATE_COMPLETED) {
                    caseEvent = new CaseCloseEvent(identityProvider.getName(), caseId, caseFile, "");
                } else {
                    caseEvent = new CaseCancelEvent(identityProvider.getName(), caseId, caseFile, Arrays.asList(event.getProcessInstance().getId()));
                }
                
                notifyParentOnCompletion(caseEvent);
            }
        }
    }

    protected void notifyParentOnCompletion(CaseEvent event) {
        CaseFileInstanceImpl caseFileInstance = (CaseFileInstanceImpl) event.getCaseFile();
        if (caseFileInstance == null) {
            return;
        }
        if (caseFileInstance.getParentInstanceId() != null && caseFileInstance.getParentWorkItemId() != null) {
            logger.debug("Case {} has defined parent information instance {}, work item {}, going to notify it", event.getCaseId(), caseFileInstance.getParentInstanceId(), caseFileInstance.getParentWorkItemId());
            ProcessService processService = (ProcessService) ServiceRegistry.get().service(ServiceRegistry.PROCESS_SERVICE);
            
            Map<String, Object> results = new HashMap<>(caseFileInstance.getData());
            results.put("CaseId", event.getCaseId());
            processService.completeWorkItem(caseFileInstance.getParentWorkItemId(), results);
            logger.debug("Parent instance id {}, work item id {}, has been successfully notified about case {} completion", caseFileInstance.getParentInstanceId(), caseFileInstance.getParentWorkItemId(), event.getCaseId());
            
            caseFileInstance.setParentInstanceId(null);
            caseFileInstance.setParentWorkItemId(null);
        }
    }
    
    protected CaseFileInstance getCaseFile(KieSession ksession) {
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() == 0) {
            return null;
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next(); 
        
        return caseFile;
    }

    @Override
    public void close() {
        // no-op
    }

}
