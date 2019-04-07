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
import java.util.stream.Collectors;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EscalateToAdminSLAViolationListener extends DefaultProcessEventListener implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(EscalateToAdminSLAViolationListener.class); 
   
    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {  
        
        CaseFileInstance caseFile = getCaseFile((KieSession) event.getKieRuntime());
        if (caseFile != null) {
            String caseId = ((WorkflowProcessInstanceImpl) event.getProcessInstance()).getCorrelationKey();
            if (caseFile.getCaseId().equals(caseId)) {
                try {
                    Collection<OrganizationalEntity> adminAssignments = ((CaseAssignment) caseFile).getAssignments("admin");
                    
                    String users = adminAssignments.stream()
                            .filter(oe -> oe instanceof User)
                            .map(oe -> oe.getId())
                            .collect(Collectors.joining(","));
                    String groups = adminAssignments.stream()
                            .filter(oe -> oe instanceof Group)
                            .map(oe -> oe.getId())
                            .collect(Collectors.joining(","));
                    String taskName = "SLA violation for case " + caseId;
                    String taskDescription = "Service Level Agreement has been violated for case " + caseId;
                    if (event.getNodeInstance() != null) {
                        taskName += "Task ("  + event.getNodeInstance().getNodeName() + ") SLA violation for case " + caseId;
                        taskDescription += " on task " + event.getNodeInstance().getNodeName();
                    }
                    
                    logger.debug("Case instance {} has SLA violation, escalating to administrator", caseId);
                    CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
                    TaskSpecification taskSpec = caseService.newHumanTaskSpec(taskName,
                                                                              taskDescription, 
                                                                              users, 
                                                                              groups, 
                                                                              null);
                    caseService.addDynamicTask(caseId, taskSpec);
                } catch (IllegalArgumentException e) {
                    logger.debug("There is no admin role defined in case instance {}, unable to escalate SLA violation", caseId);
                }
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
