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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.ClassObjectFilter;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Work item handler that allows to start new case instance based on following parameters:
 * <ul>
 *  <li><b>DeploymentId</b> - deployment id where that case definition belongs to (if not given deployment id of the work item will be used)</li>
 *  <li><b>CaseDefinitionId</b> - identifier of the case definition a new case instance should be started for</li>
 *  <li><b>Data_NAME</b> - case file data to be given when starting a case - NAME is the name of the case file input, can be given as many times as needed</li>
 *  <li><b>UserRole_NAME</b> - case role assignment as user entity where NAME is the name of the role that given user should be assigned to</li>
 *  <li><b>GroupRole_NAME</b> - case role assignment as group entity where NAME is the name of the role that given group should be assigned to</li>
 *  <li><b>DataAccess_NAME</b> - case file data access restriction where NAME is the name of the data that given roles should have access to, supports list of roles (comma separated)</li>
 *  <li><b>Independent</b> - indicates if the case instance is independent of the node that starts it - default is false</li>
 *  <li><b>DestroyOnAbort</b> - indicates if the case instance should be destroyed in case the work item is aborted, defaults to true</li>
 * </ul>
 * This work item allows to be executed in independent mode which means once the case instance is started it will complete work item without waiting for case instance completion. 
 * In this scenario work item is completed with data taken from the case instance - data from case file plus case id that is stored under <i>CaseId</i> name in the result map.
 * <br/>
 * <br/>
 * If the work item is (as by default) in the dependent mode, this work item will not be completed until case instance completes - either being closed or canceled/destroyed.
 * <br/>
 * <br/>
 * Regardless of the mode in which this handler executes work items <i>CaseId</i> is always returned as part of the result of work item completion. 
 * Additionally case file data are also returned on completion - data that are available at the time when work item is being completed.
 */
public class StartCaseWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(StartCaseWorkItemHandler.class);
    
    private static final String DEPLOYMENT_ID = "DeploymentId";
    private static final String CASE_DEFINITION_ID = "CaseDefinitionId";
    private static final String DATA_PREFIX = "Data_";
    private static final String USER_ROLE_PREFIX = "UserRole_";
    private static final String GROUP_ROLE_PREFIX = "GroupRole_";
    private static final String DATA_ACCESS_PREFIX = "DataAccess_";
    private static final String INDEPENDENT = "Independent";
    private static final String DESTROY_ON_ABORT = "DestroyOnAbort";
    
    private static final String CASE_ID = "CaseId";
    
    private KieSession ksession;
    
    public StartCaseWorkItemHandler(KieSession ksession) {
        this.ksession = ksession;
    }
    
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String deploymentId = (String) workItem.getParameter(DEPLOYMENT_ID);
        if (deploymentId == null) {
            deploymentId = ((org.drools.core.process.instance.WorkItem)workItem).getDeploymentId();
        }
        
        RuntimeManager targetRuntimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
        if (targetRuntimeManager == null || !(targetRuntimeManager instanceof PerCaseRuntimeManager)) {
            throw new IllegalArgumentException("Requested target deployment does not exist or is not per case strategy");
        }
        
        String caseDefinitionId = (String) workItem.getParameter(CASE_DEFINITION_ID);
        if (caseDefinitionId == null || caseDefinitionId.trim().isEmpty()) {
            throw new IllegalArgumentException(CASE_DEFINITION_ID + " is a required parameter for StartCaseWorkItemHandler");
        }
        
        
        Map<String, Object> caseFileData = new HashMap<>();
        Map<String, List<String>> accessRestrictions = new HashMap<>();
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        
        parseParameters(workItem, caseFileData, roleAssignments, accessRestrictions);        
       
        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
        long workItemId = workItem.getId();
        logger.debug("Parent process instance id {} and work item instance id {} for new case instance", processInstanceId, workItemId);
        
        CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
        
        CaseFileInstance subCaseFile = caseService.newCaseFileInstanceWithRestrictions(deploymentId, caseDefinitionId, caseFileData, roleAssignments, accessRestrictions);
        ((CaseFileInstanceImpl) subCaseFile).setParentInstanceId(processInstanceId);
        ((CaseFileInstanceImpl) subCaseFile).setParentWorkItemId(workItemId);
        
        String caseId = caseService.startCase(deploymentId, caseDefinitionId, subCaseFile);        
        logger.debug("Case with id {} has been successfully started");
        
        boolean independent = Boolean.parseBoolean((String) workItem.getParameter(INDEPENDENT));
        if (independent) {
            Map<String, Object> results = new HashMap<>();
            results.put(CASE_ID, caseId);
            try {
                CaseFileInstance snapshot = caseService.getCaseFileInstance(caseId);
                
                results.putAll(snapshot.getData());
            } catch (CaseNotFoundException e) {
                // case is already completed
                logger.debug("Case is already completed, not possible to fetch case file data any more");
            }
            logger.debug("Completing directly (without waiting for case instance {} completion) work item with id {}", caseId, workItem.getId()); 
            ((CaseFileInstanceImpl) subCaseFile).setParentInstanceId(null);
            ((CaseFileInstanceImpl) subCaseFile).setParentWorkItemId(null);
            manager.completeWorkItem(workItem.getId(), results);
        } else {
            // save case id so the abort work item can abort/destroy the case instance
            ((WorkItemImpl)workItem).setParameter(CASE_ID, caseId);
            logger.debug("Waiting for case instance {} completion before completing work item with id {}", caseId, workItem.getId());
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        String caseId = (String) workItem.getParameter(CASE_ID);
        boolean destroy = true;
        if (workItem.getParameter(DESTROY_ON_ABORT) != null) {
            destroy = Boolean.parseBoolean((String) workItem.getParameter(DESTROY_ON_ABORT));
        }
                
        CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
        
        try {
            if (destroy) {
                logger.debug("Case {} is going to be destroyed", caseId);
                caseService.destroyCase(caseId);
            } else {
                logger.debug("Case {} is going to be canceled", caseId);
                caseService.cancelCase(caseId);
            }
        } catch (CaseNotFoundException e) {
            logger.warn("Case instance {} was not found", caseId);
        } catch (Exception e) {
            logger.error("Unexpected error during canceling case instance {}", caseId, e);
        }
    }
    
    protected CaseFileInstance getCaseFile(KieSession ksession) {
        if (ksession == null) {
            logger.debug("No ksession available returning null as case file");
            return null;
        }
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() == 0) {
            logger.debug("No case file available in working memory returning null as case file");
            return null;
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next(); 
        
        return caseFile;
    }
    
    protected void parseParameters(WorkItem workItem, Map<String, Object> caseFileData, Map<String, OrganizationalEntity> roleAssignments, Map<String, List<String>> accessRestrictions) {
        TaskModelFactory taskModelFactory = TaskModelProvider.getFactory(); 
        CaseFileInstance caseFile = getCaseFile(ksession);
        
        
        for (Entry<String, Object> entry : workItem.getParameters().entrySet()) {
            if (entry.getKey().startsWith(DATA_PREFIX)) {
                String name = entry.getKey().replaceFirst(DATA_PREFIX, "");
                caseFileData.put(name, entry.getValue());
                logger.debug("Added {} item to case file with value {}", name, entry.getValue());
                
            } else if (entry.getKey().startsWith(USER_ROLE_PREFIX)) {
                
                String name = entry.getKey().replaceFirst(USER_ROLE_PREFIX, "");
                User user = taskModelFactory.newUser((String) entry.getValue());
                if (caseFile != null) {
                    try {
                        Collection<OrganizationalEntity> caseAssignments = ((CaseAssignment)caseFile).getAssignments(name);
                        
                        user = (User) caseAssignments.stream()
                                .filter(oe -> oe instanceof User)
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException());
                    } catch (IllegalArgumentException e) { 
                        logger.debug("no such role {} or there is no user found for given role name", name);
                    }
                } 
                roleAssignments.put(name, user);
                logger.debug("Added user {} as assignment to the role {}", entry.getValue(), entry.getKey());
                
            } else if (entry.getKey().startsWith(GROUP_ROLE_PREFIX)) {
                
                String name = entry.getKey().replaceFirst(GROUP_ROLE_PREFIX, "");                
                Group group = taskModelFactory.newGroup((String) entry.getValue());
                if (caseFile != null) {
                    try {
                        Collection<OrganizationalEntity> caseAssignments = ((CaseAssignment)caseFile).getAssignments(name);
                        
                        group = (Group) caseAssignments.stream()
                                .filter(oe -> oe instanceof Group)
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException());
                    } catch (IllegalArgumentException e) {
                        logger.debug("no such role {} or there is no group found for given role name", name); 
                    }
                } 
                roleAssignments.put(name, group);
                logger.debug("Added group {} as assignment to the role {}", entry.getValue(), entry.getKey());
                
            } else if (entry.getKey().startsWith(DATA_ACCESS_PREFIX)) {
                String name = entry.getKey().replaceFirst(DATA_ACCESS_PREFIX, "");
                String[] roles = ((String) entry.getValue()).split(",");
                List<String> restrictedTo = new ArrayList<>(Arrays.asList(roles));
                
                accessRestrictions.put(name, restrictedTo);
                logger.debug("Added access restriction for {} with following roles {}", name, restrictedTo);
                
            } 
        }
    }

}
