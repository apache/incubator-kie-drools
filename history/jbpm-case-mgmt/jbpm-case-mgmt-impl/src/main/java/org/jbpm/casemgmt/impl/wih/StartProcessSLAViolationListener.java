/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StartProcessSLAViolationListener extends DefaultProcessEventListener implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(StartProcessSLAViolationListener.class); 
   
    private String processId;
    
    public StartProcessSLAViolationListener(String processId) {
        this.processId = processId;
    }
    
    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {  

        CaseFileInstance caseFile = getCaseFile((KieSession) event.getKieRuntime());
        if (caseFile != null) {
            String caseId = ((WorkflowProcessInstanceImpl) event.getProcessInstance()).getCorrelationKey();
            if (caseFile.getCaseId().equals(caseId)) {
                   
                logger.debug("Case instance {} has SLA violation, escalating starting new process instance for {}", caseId, processId);
                CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
                
                Long slaViolationProcessInstanceId = caseService.addDynamicSubprocess(caseId, processId, null);
                logger.debug("Process instance with id {} was created to handle SLA violation for case {}", slaViolationProcessInstanceId, caseId);           
            }
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
