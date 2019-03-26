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

package org.jbpm.casemgmt.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.AdHocFragmentNotFoundException;
import org.jbpm.casemgmt.api.CaseActiveException;
import org.jbpm.casemgmt.api.CaseDefinitionNotFoundException;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.StageNotFoundException;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.auth.AuthorizationManager.ProtectedOperation;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.api.model.instance.CommentSortBy;
import org.jbpm.casemgmt.impl.command.AddDataCaseFileInstanceCommand;
import org.jbpm.casemgmt.impl.command.AddDynamicProcessCommand;
import org.jbpm.casemgmt.impl.command.AddDynamicProcessToStageCommand;
import org.jbpm.casemgmt.impl.command.AddDynamicTaskCommand;
import org.jbpm.casemgmt.impl.command.AddDynamicTaskToStageCommand;
import org.jbpm.casemgmt.impl.command.CancelCaseCommand;
import org.jbpm.casemgmt.impl.command.CaseCommentCommand;
import org.jbpm.casemgmt.impl.command.CloseCaseCommand;
import org.jbpm.casemgmt.impl.command.ModifyRoleAssignmentCommand;
import org.jbpm.casemgmt.impl.command.RemoveDataCaseFileInstanceCommand;
import org.jbpm.casemgmt.impl.command.ReopenCaseCommand;
import org.jbpm.casemgmt.impl.command.StartCaseCommand;
import org.jbpm.casemgmt.impl.command.TriggerAdHocNodeInStageCommand;
import org.jbpm.casemgmt.impl.dynamic.HumanTaskSpecification;
import org.jbpm.casemgmt.impl.dynamic.WorkItemTaskSpecification;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CaseInstanceImpl;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.KieInternalServices;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.utils.LazyLoaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseServiceImpl implements CaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);
    
    private CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    private KieCommands commandsFactory = KieServices.Factory.get().getCommands();
    private TaskModelFactory factory = TaskModelProvider.getFactory();
    
    private CaseIdGenerator caseIdGenerator;
    
    private ProcessService processService;
    private RuntimeDataService runtimeDataService;
    private DeploymentService deploymentService;
    private CaseRuntimeDataService caseRuntimeDataService;
    private TransactionalCommandService commandService;
    private AuthorizationManager authorizationManager;
    private IdentityProvider identityProvider;
    
    private CaseEventSupport emptyCaseEventSupport = new CaseEventSupport(null, Collections.emptyList());
    
    
    public CaseServiceImpl() {
        ServiceRegistry.get().register(CaseService.class.getSimpleName(), this);
    }
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }

    public void setDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }
    
    public void setCaseRuntimeDataService(CaseRuntimeDataService caseRuntimeDataService) {
        this.caseRuntimeDataService = caseRuntimeDataService;
    }
    
    public void setCaseIdGenerator(CaseIdGenerator caseIdGenerator) {
        this.caseIdGenerator = caseIdGenerator;
    }
    
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
    
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }
    
    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
        this.emptyCaseEventSupport = new CaseEventSupport(identityProvider, Collections.emptyList());
    }

    @Override
    public String startCase(String deploymentId, String caseDefinitionId) {
        
        return startCase(deploymentId, caseDefinitionId, null);
    }

    @Override
    public String startCase(String deploymentId, String caseDefinitionId, CaseFileInstance caseFile) {
        
        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentId, caseDefinitionId);
        if (caseDef == null) {
            throw new CaseNotFoundException("Case definition " + caseDefinitionId + " not found");
        }
        String caseId = caseIdGenerator.generate(caseDef.getIdentifierPrefix(), (caseFile == null ? new HashMap<>() : caseFile.getData()));
        logger.debug("Generated case id {} for case definition id {}", caseId, caseDefinitionId);
        
        if (caseFile == null) {
            caseFile = new CaseFileInstanceImpl(caseId, caseDefinitionId);
            ((CaseFileInstanceImpl)caseFile).setupRoles(caseDef.getCaseRoles());     
            logger.debug("CaseFile was not given, creating new empty one.");
        } else {
            ((CaseFileInstanceImpl)caseFile).setCaseId(caseId);
            logger.debug("CaseFile {} was given, associating it with case {}", caseFile, caseId);
        }

        //If owner is provided in the case file use that, otherwise default to current logged in user.
        boolean hasOwner = ((CaseFileInstanceImpl) caseFile).getAssignments().stream().anyMatch(role -> role.getRoleName().equals(AuthorizationManager.OWNER_ROLE));
        if(hasOwner == false){
            ((CaseFileInstanceImpl)caseFile).assignOwner(newUser(identityProvider.getName()));
        }

        processService.execute(deploymentId, CaseContext.get(caseId), new StartCaseCommand(identityProvider, caseId, deploymentId, caseDefinitionId, caseFile, processService));
        
        return caseId;
    }
    
    @Override
    public CaseFileInstance getCaseFileInstance(String caseId) throws CaseNotFoundException {
        authorizationManager.checkAuthorization(caseId);
        ProcessInstanceDesc pi = verifyCaseIdExisted(caseId);

        return internalGetCaseFileInstance(caseId, pi.getDeploymentId());
    }

    @Override
    public CaseInstance getCaseInstance(String caseId) throws CaseNotFoundException {
        return getCaseInstance(caseId, false, false, false, false);
    }
    
    @Override
    public CaseInstance getCaseInstance(String caseId, boolean withData, boolean withRoles, boolean withMilestones, boolean withStages) throws CaseNotFoundException {
        authorizationManager.checkAuthorization(caseId);
        CaseInstanceImpl caseInstance = (CaseInstanceImpl) caseRuntimeDataService.getCaseInstanceById(caseId);
        
        if (caseInstance.getStatus().equals(ProcessInstance.STATE_ACTIVE)) {
           
            if (withData) {
                CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, caseInstance.getDeploymentId());
                caseInstance.setCaseFile(caseFile);
            }
            if (withMilestones) {
                Collection<CaseMilestoneInstance> milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, false, new org.kie.internal.query.QueryContext(0, 100));
                caseInstance.setCaseMilestones(milestones);
            }
            if (withRoles) {
                Collection<CaseRoleInstance> roles = getCaseRoleAssignments(caseId);
                caseInstance.setCaseRoles(roles);
            }
            if (withStages) {
                Collection<CaseStageInstance> stages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new org.kie.internal.query.QueryContext(0, 100));
                caseInstance.setCaseStages(stages);
            }
            
        } 
        
        return caseInstance;
    }   
    

    @Override
    public void closeCase(String caseId, String comment) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.CLOSE_CASE);
        logger.debug("About to close case {} with comment {}", caseId, comment);        
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CloseCaseCommand(identityProvider, pi.getDeploymentId(), caseId, comment, processService, runtimeDataService));
    }

    @Override
    public void cancelCase(String caseId) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.CANCEL_CASE);
        logger.debug("About to abort case {}", caseId);        
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CancelCaseCommand(identityProvider, caseId, processService, runtimeDataService, false));
    }

    @Override
    public void destroyCase(String caseId) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.DESTROY_CASE);
        logger.debug("About to destroy permanently case {}", caseId);  
        ProcessInstanceDesc pi = verifyCaseIdExisted(caseId);        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CancelCaseCommand(identityProvider, caseId, processService, runtimeDataService, true));
    }
    

    @Override
    public void reopenCase(String caseId, String deploymentId, String caseDefinitionId) throws CaseNotFoundException {        
        reopenCase(caseId, deploymentId, caseDefinitionId, new HashMap<>());
        
    }

    @Override
    public void reopenCase(String caseId, String deploymentId, String caseDefinitionId, Map<String, Object> data) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.REOPEN_CASE);
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));
        if (pi != null) {
            throw new CaseActiveException("Case with id " + caseId + " is still active and cannot be reopened"); 
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId);
        params.put("maxResults", 1);

        List<Long> caseIdMapping = commandService.execute(new QueryNameCommand<List<Long>>("findCaseIdContextMapping", params));
        if (caseIdMapping.isEmpty()) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found");
        }
        
        logger.debug("About to reopen case {} by starting process instance {} from deployment {} with additional data {}", 
                caseId, caseDefinitionId, deploymentId, data);
        
        processService.execute(deploymentId, CaseContext.get(caseId), new ReopenCaseCommand(identityProvider, caseId, deploymentId, caseDefinitionId, data, processService));
        
    }


    
    /*
     * Dynamic operations on a case
     */
    

    @Override
    public void addDynamicTask(String caseId, TaskSpecification taskSpecification) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_TASK_TO_CASE);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicTaskCommand(identityProvider, caseId, taskSpecification.getNodeType(), pi.getId(), taskSpecification.getParameters()));
    }
    
    @Override
    public void addDynamicTask(Long processInstanceId, TaskSpecification taskSpecification) throws ProcessInstanceNotFoundException {        
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_TASK_TO_CASE);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicTaskCommand(identityProvider, caseId, taskSpecification.getNodeType(), pi.getId(), taskSpecification.getParameters()));
    }

    @Override
    public void addDynamicTaskToStage(String caseId, String stage, TaskSpecification taskSpecification) throws CaseNotFoundException, StageNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_TASK_TO_CASE);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + pi.getId() + " or it's not active anymore");
        }
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicTaskToStageCommand(identityProvider, caseId, taskSpecification.getNodeType(), pi.getId(), stage, taskSpecification.getParameters()));
    }
    

    @Override
    public void addDynamicTaskToStage(Long processInstanceId, String stage, TaskSpecification taskSpecification) throws CaseNotFoundException, StageNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_TASK_TO_CASE);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicTaskToStageCommand(identityProvider, caseId, taskSpecification.getNodeType(), pi.getId(), stage, taskSpecification.getParameters()));        
    }
    

    @Override
    public Long addDynamicSubprocess(String caseId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_PROCESS_TO_CASE);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + pi.getId() + " or it's not active anymore");
        }
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicProcessCommand(identityProvider, caseId, pi.getId(), processId, parameters));
    }

    @Override
    public Long addDynamicSubprocess(Long processInstanceId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        
        String caseId = pi.getCorrelationKey();
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_PROCESS_TO_CASE);
        
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicProcessCommand(identityProvider, caseId, pi.getId(), processId, parameters));
    }
    
    @Override
    public Long addDynamicSubprocessToStage(String caseId, String stage, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_PROCESS_TO_CASE);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicProcessToStageCommand(identityProvider, caseId, pi.getId(), stage, processId, parameters));
    }

    @Override
    public Long addDynamicSubprocessToStage(Long processInstanceId, String stage, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_PROCESS_TO_CASE);
        
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicProcessToStageCommand(identityProvider, caseId, pi.getId(), stage, processId, parameters));
    }
    
    @Override
    public void triggerAdHocFragment(String caseId, String fragmentName, Object data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        triggerAdHocFragment(pi.getId(), fragmentName, data);
    }

    @Override
    public void triggerAdHocFragment(Long processInstanceId, String fragmentName, Object data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        internalTriggerAdHocFragment(pi, fragmentName, data);
    }
    
    @Override
    public void triggerAdHocFragment(String caseId, String stage, String fragmentName, Object data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        triggerAdHocFragment(pi.getId(), stage, fragmentName, data);
    }

    @Override
    public void triggerAdHocFragment(Long processInstanceId, String stage, String fragmentName, Object data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }

        CaseDefinition caseDef = caseRuntimeDataService.getCase(pi.getDeploymentId(), pi.getProcessId());
        
        CorrelationKey key = CorrelationKeyXmlAdapter.unmarshalCorrelationKey(pi.getCorrelationKey());
        String caseId = (String) key.getProperties().get(0).getValue();
        authorizationManager.checkAuthorization(caseId);        
                
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new TriggerAdHocNodeInStageCommand(identityProvider, caseDef, pi.getId(), stage, fragmentName, data));
    }
   

    /*
     * Case file data methods
     */

    @Override
    public void addDataToCaseFile(String caseId, String name, Object value, String... restrictedTo) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_DATA);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        List<String> accessRestriction = null;
        if (restrictedTo != null) {
            accessRestriction = Arrays.asList(restrictedTo);
        }
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(name, value);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDataCaseFileInstanceCommand(pi.getDeploymentId(), pi.getId(), identityProvider, parameters, accessRestriction, authorizationManager, processService));        
    }

    @Override
    public void addDataToCaseFile(String caseId, Map<String, Object> data, String... restrictedTo) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.ADD_DATA);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        List<String> accessRestriction = null;
        if (restrictedTo != null) {
            accessRestriction = Arrays.asList(restrictedTo);
        }
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDataCaseFileInstanceCommand(pi.getDeploymentId(), pi.getId(), identityProvider, data, accessRestriction, authorizationManager, processService));
    }
    
    @Override
    public void removeDataFromCaseFile(String caseId, String name) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.REMOVE_DATA);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new RemoveDataCaseFileInstanceCommand(identityProvider, Arrays.asList(name), authorizationManager));
        
    }

    @Override
    public void removeDataFromCaseFile(String caseId, List<String> variableNames) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.REMOVE_DATA);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new RemoveDataCaseFileInstanceCommand(identityProvider, variableNames, authorizationManager));
    }

    /*
     * Case role assignment methods
     */

    @Override
    public void assignToCaseRole(String caseId, String role, OrganizationalEntity entity) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_ROLE_ASSIGNMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new ModifyRoleAssignmentCommand(identityProvider, role, entity, true));
    }

    @Override
    public void removeFromCaseRole(String caseId, String role, OrganizationalEntity entity) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_ROLE_ASSIGNMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new ModifyRoleAssignmentCommand(identityProvider, role, entity, false));
    }

    @Override
    public Collection<CaseRoleInstance> getCaseRoleAssignments(String caseId) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_ROLE_ASSIGNMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, pi.getDeploymentId());
        
        return ((CaseFileInstanceImpl)caseFile).getAssignments();
    }

    
    /*
     * Case comments methods
     */
    

    @Override
    public Collection<CommentInstance> getCaseComments(String caseId, QueryContext queryContext) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_COMMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);

        CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, pi.getDeploymentId());

        List<CommentInstance> caseComments = new ArrayList<>(((CaseFileInstanceImpl) caseFile).getComments());
        // apply authorization
        caseComments = authorizationManager.filterByCommentAuthorization(caseId, caseFile, caseComments);
        
        int caseCommentsSize = caseComments.size();

        int offset = queryContext.getOffset();
        int pageSize = queryContext.getCount();

        int pageIndex = (caseCommentsSize + pageSize - 1) / pageSize;

        if (caseCommentsSize < pageSize) {
            return caseComments;
        } else if (pageIndex == (offset/pageSize) + 1) {
            return caseComments.subList(offset, caseCommentsSize);
        } else {
            return caseComments.subList(offset, offset + pageSize);
        }
    }

    @Override
    public Collection<CommentInstance> getCaseComments(String caseId, CommentSortBy sortBy, QueryContext queryContext) throws CaseNotFoundException {
        authorizationManager.checkAuthorization(caseId);
        Collection<CommentInstance> comments = getCaseComments(caseId, queryContext);
        
        return comments.stream().sorted((o1, o2) -> {
            int result = 0;
            switch (sortBy) {
                case Date : 
                    result = o1.getCreatedAt().compareTo(o2.getCreatedAt());
                    break;
                case Author : 
                    result = o1.getAuthor().compareTo(o2.getAuthor());
                    break;
            }
            return result;
        }).collect(Collectors.toList());          
    }

    @Override
    public String addCaseComment(String caseId, String author, String comment, String... restrictedTo) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_COMMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        List<String> accessRestriction = null;
        if (restrictedTo != null && restrictedTo.length > 0) {
            accessRestriction = Arrays.asList(restrictedTo);
        }
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(identityProvider, author, comment, accessRestriction));
    }
    
    @Override
    public void updateCaseComment(String caseId, String commentId, String author, String text, String... restrictedTo) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_COMMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        List<String> accessRestriction = null;
        if (restrictedTo != null && restrictedTo.length > 0) {
            accessRestriction = Arrays.asList(restrictedTo);
        }
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(identityProvider, commentId, author, text, accessRestriction, authorizationManager));
        
    }

    @Override
    public void removeCaseComment(String caseId, String commentId) throws CaseNotFoundException {
        authorizationManager.checkOperationAuthorization(caseId, ProtectedOperation.MODIFY_COMMENT);
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(identityProvider, commentId, authorizationManager));
    }

    /*
     * new instances methods
     */
    
    @Override
    public CaseFileInstance newCaseFileInstance(String deploymentId, String caseDefinition, Map<String, Object> data) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        if (def == null) {
            throw new CaseDefinitionNotFoundException("Case definition " + caseDefinition + " does not exist in deployment " + deploymentId);
        }
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(caseDefinition, data);
        caseFile.setupRoles(def.getCaseRoles());
        caseFile.setAccessRestrictions(def.getDataAccessRestrictions());
        
        return caseFile;
    }
    
    @Override
    public CaseFileInstance newCaseFileInstanceWithRestrictions(String deploymentId, String caseDefinition, Map<String, Object> data, Map<String, List<String>> accessRestrictions) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        if (def == null) {
            throw new CaseDefinitionNotFoundException("Case definition " + caseDefinition + " does not exist in deployment " + deploymentId);
        }
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(caseDefinition, data);
        caseFile.setupRoles(def.getCaseRoles());
        
        Map<String, List<String>> combinedAccessRestrictions = def.getDataAccessRestrictions();
        if (accessRestrictions != null) {
            combinedAccessRestrictions.putAll(accessRestrictions);
        }
        caseFile.setAccessRestrictions(combinedAccessRestrictions);
        
        return caseFile;
    }
    
    @Override
    public CaseFileInstance newCaseFileInstance(String deploymentId, String caseDefinition, Map<String, Object> data, Map<String, OrganizationalEntity> rolesAssignment) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        if (def == null) {
            throw new CaseDefinitionNotFoundException("Case definition " + caseDefinition + " does not exist in deployment " + deploymentId);
        }
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(caseDefinition, data);
        caseFile.setupRoles(def.getCaseRoles());
        caseFile.setAccessRestrictions(def.getDataAccessRestrictions());
        
        rolesAssignment.entrySet().stream().forEach(entry -> caseFile.assign(entry.getKey(), entry.getValue()));
        
        return caseFile;
    }
    
    @Override
    public CaseFileInstance newCaseFileInstanceWithRestrictions(String deploymentId, String caseDefinition, Map<String, Object> data, Map<String, OrganizationalEntity> rolesAssignment, Map<String, List<String>> accessRestrictions) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        if (def == null) {
            throw new CaseDefinitionNotFoundException("Case definition " + caseDefinition + " does not exist in deployment " + deploymentId);
        }
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(caseDefinition, data);
        caseFile.setupRoles(def.getCaseRoles());
        Map<String, List<String>> combinedAccessRestrictions = def.getDataAccessRestrictions();
        if (accessRestrictions != null) {
            combinedAccessRestrictions.putAll(accessRestrictions);
        }
        caseFile.setAccessRestrictions(combinedAccessRestrictions);
        
        rolesAssignment.entrySet().stream().forEach(entry -> caseFile.assign(entry.getKey(), entry.getValue()));
        
        return caseFile;
    }

    @Override
    public TaskSpecification newHumanTaskSpec(String taskName, String description, String actorIds, String groupIds, Map<String, Object> parameters) {        
        return new HumanTaskSpecification(taskName, actorIds, groupIds, description, parameters);
    }

    @Override
    public TaskSpecification newTaskSpec(String nodeType, String nodeName, Map<String, Object> parameters) {
        return new WorkItemTaskSpecification(nodeType, nodeName, parameters);
    }

    @Override
    public User newUser(String userId) {
        return factory.newUser(userId);
    }

    @Override
    public Group newGroup(String groupId) {
        return factory.newGroup(groupId);
    }
    
    /*
     * internal methods
     */

    @SuppressWarnings("unchecked")
    protected CaseFileInstance internalGetCaseFileInstance(String caseId, String deploymentId) {
        logger.debug("Retrieving case file from working memory for case " + caseId);
        Collection<CaseFileInstance> caseFiles = (Collection<CaseFileInstance>) processService.execute(deploymentId, CaseContext.get(caseId), commandsFactory.newGetObjects(new ClassObjectFilter(CaseFileInstance.class)));
        if (caseFiles.size() == 0) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found");
        } else if (caseFiles.size() == 1) {
            CaseFileInstance caseFile =  caseFiles.iterator().next();
            logger.debug("Single case file {} found in working memory", caseFile);
            
            // apply authorization
            Map<String, Object> filteredData = authorizationManager.filterByDataAuthorization(caseId, caseFile, caseFile.getData());
            ((CaseFileInstanceImpl)caseFile).setData(filteredData);
            
            for (Object variable : caseFile.getData().values()) {
                if (variable instanceof LazyLoaded<?>) {
                    ((LazyLoaded<?>) variable).load();
                }
            }
            
            return caseFile;
        } 
        logger.warn("Multiple case files found in working memory (most likely not using PER_CASE strategy), trying to filter out...");
        CaseFileInstance caseFile = caseFiles.stream()
                .filter(cf -> cf.getCaseId().equals(caseId))
                .findFirst()
                .orElse(null);
        logger.warn("Case file {} after filtering {}", caseFile, (caseFile == null?"not found" : "found"));
        
        if (caseFile != null) {
            // apply authorization
            Map<String, Object> filteredData = authorizationManager.filterByDataAuthorization(caseId, caseFile, caseFile.getData());
            ((CaseFileInstanceImpl)caseFile).setData(filteredData);
            

            for (Object variable : caseFile.getData().values()) {
                if (variable instanceof LazyLoaded<?>) {
                    ((LazyLoaded<?>) variable).load();
                }
            }
        }
        
        
        return caseFile;
    }
    
    protected void internalTriggerAdHocFragment(ProcessInstanceDesc pi, String fragmentName, Object data) throws CaseNotFoundException {
        
        CorrelationKey key = CorrelationKeyXmlAdapter.unmarshalCorrelationKey(pi.getCorrelationKey());
        String caseId = (String) key.getProperties().get(0).getValue();
        authorizationManager.checkAuthorization(caseId);
        
        CaseDefinition caseDef = caseRuntimeDataService.getCase(pi.getDeploymentId(), pi.getProcessId());
        List<AdHocFragment> allFragments = new ArrayList<>();
        if (caseDef.getAdHocFragments() != null) {
            allFragments.addAll(caseDef.getAdHocFragments());
        }
        caseDef.getCaseStages().forEach(stage -> {
            if (stage.getAdHocFragments() != null) {
                allFragments.addAll(stage.getAdHocFragments());
            } 
        });
        
        allFragments.stream()
        .filter(fragment -> fragment.getName() != null && fragment.getName().equals(fragmentName))
        .findFirst()
        .orElseThrow(() -> new AdHocFragmentNotFoundException("AdHoc fragment '" + fragmentName + "' not found in case " + pi.getCorrelationKey()));
        
        processService.signalProcessInstance(pi.getId(), fragmentName, data);
    }
    
    /*
     * helper method
     */
    protected ProcessInstanceDesc verifyCaseIdExists(String caseId) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));
        if (pi == null) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found"); 
        }
        
        return pi;
    }
    
    protected ProcessInstanceDesc verifyCaseIdExisted(String caseId) throws CaseNotFoundException {
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstancesByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId), new org.kie.api.runtime.query.QueryContext(0, 1));
        if (instances.isEmpty()) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found"); 
        }
        
        return instances.iterator().next();
    }


    protected CaseEventSupport getCaseEventSupport(String deploymentId) {
        RuntimeManager runtimeManager = deploymentService.getRuntimeManager(deploymentId);
        if (runtimeManager instanceof PerCaseRuntimeManager) {
            CaseEventSupport caseEventSupport = (CaseEventSupport) ((PerCaseRuntimeManager) runtimeManager).getCaseEventSupport();
            if (caseEventSupport != null) {
                return caseEventSupport;
            }
        }
        return emptyCaseEventSupport;
    }

    protected boolean isEmpty(Collection<?> data) {
        if (data == null || data.isEmpty()) {
            return true;
        }
        
        return false;
    }
   

}
