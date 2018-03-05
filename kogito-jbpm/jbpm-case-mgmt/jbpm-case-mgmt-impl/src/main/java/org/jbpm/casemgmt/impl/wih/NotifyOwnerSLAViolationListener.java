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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NotifyOwnerSLAViolationListener extends DefaultProcessEventListener implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(NotifyOwnerSLAViolationListener.class); 
   
    private UserInfo userInfo = UserDataServiceProvider.getUserInfo();
    
    private String subject;
    private String body;
    private String template;
    
    
    public NotifyOwnerSLAViolationListener() {     
    }        
    
    public NotifyOwnerSLAViolationListener(String subject, String body, String template) {
        super();
        this.subject = subject;
        this.body = body;
        this.template = template;
    }



    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {  
        CaseFileInstance caseFile = getCaseFile((KieSession) event.getKieRuntime());
        if (caseFile != null) {
            String caseId = ((WorkflowProcessInstanceImpl) event.getProcessInstance()).getCorrelationKey();
            if (caseFile.getCaseId().equals(caseId)) {
                try {
                                        
                    Collection<OrganizationalEntity> adminAssignments = ((CaseAssignment) caseFile).getAssignments("owner");
                    
                    String recipients = adminAssignments.stream()
                            .map(oe -> userInfo.getEmailForEntity(oe))
                            .collect(Collectors.joining(";"));
        
                    
                    logger.debug("Case instance {} has SLA violation, notifying owner", caseId);
                    CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
                    Map<String, Object> parameters = buildEmailParameters(recipients, caseId, event);
                    
                    TaskSpecification taskSpec = caseService.newTaskSpec("Email", "SLA Violation for case " + caseId, parameters);
                    caseService.addDynamicTask(caseId, taskSpec);
                } catch (IllegalArgumentException e) {
                    logger.debug("There is no owner role defined in case instance {}, unable to notify SLA violation", caseId);
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
    
    protected Map<String, Object> buildEmailParameters(String recipients, String caseId, SLAViolatedEvent event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("To", recipients);
        parameters.put("Subject", this.subject == null ? "SLA Violation for case " + caseId : this.subject);
        parameters.put("Body", this.body == null ? "Service Level Agreement has been violated for case " + caseId : this.body);
        
        if (this.template != null) {
            parameters.put("Template", this.template);
        }
        
        return parameters;
    }

}
