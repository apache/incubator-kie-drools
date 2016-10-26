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

package org.jbpm.casemgmt.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.ClassObjectFilter;
import org.jbpm.casemgmt.api.CaseActiveException;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.StageNotFoundException;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
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
import org.jbpm.casemgmt.impl.command.ModifyRoleAssignmentCommand;
import org.jbpm.casemgmt.impl.command.RemoveDataCaseFileInstanceCommand;
import org.jbpm.casemgmt.impl.command.ReopenCaseCommand;
import org.jbpm.casemgmt.impl.command.StartCaseCommand;
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
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.query.QueryContext;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
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
    
    private CaseEventSupport emptyCaseEventSupport = new CaseEventSupport(Collections.emptyList());
    
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
            caseFile = new CaseFileInstanceImpl(caseId);
            ((CaseFileInstanceImpl)caseFile).setupRoles(caseDef.getCaseRoles());     
            logger.debug("CaseFile was not given, creating new empty one.");
        } else {
            ((CaseFileInstanceImpl)caseFile).setCaseId(caseId);
            logger.debug("CaseFile {} was given, associating it with case {}", caseFile, caseId);
        }
        
        processService.execute(deploymentId, CaseContext.get(caseId), new StartCaseCommand(caseId, deploymentId, caseDefinitionId, caseFile, processService));
        
        return caseId;
    }
    
    @Override
    public CaseFileInstance getCaseFileInstance(String caseId) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExisted(caseId);

        return internalGetCaseFileInstance(caseId, pi);
    }

    @Override
    public CaseInstance getCaseInstance(String caseId) throws CaseNotFoundException {
        return getCaseInstance(caseId, false, false, false, false);
    }
    
    @Override
    public CaseInstance getCaseInstance(String caseId, boolean withData, boolean withRoles, boolean withMilestones, boolean withStages) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        if (pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {

            CaseInstanceImpl caseInstance = new CaseInstanceImpl(caseId, pi.getProcessInstanceDescription(), pi.getProcessId(), pi.getState(), pi.getDeploymentId(), pi.getInitiator(), pi.getDataTimeStamp(), null, pi.getId(), "");
            if (withData) {
                CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, pi);
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
            
            return caseInstance;
        } else {
            return null;
        }
    }    

    @Override
    public void cancelCase(String caseId) throws CaseNotFoundException {
        logger.debug("About to abort case {}", caseId);        
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CancelCaseCommand(caseId, processService, runtimeDataService, false));
    }

    @Override
    public void destroyCase(String caseId) throws CaseNotFoundException {
        logger.debug("About to destroy permanently case {}", caseId);  
        ProcessInstanceDesc pi = verifyCaseIdExisted(caseId);        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CancelCaseCommand(caseId, processService, runtimeDataService, true));
    }
    

    @Override
    public void reopenCase(String caseId, String deploymentId, String caseDefinitionId) throws CaseNotFoundException {
        reopenCase(caseId, deploymentId, caseDefinitionId, new HashMap<>());
        
    }

    @Override
    public void reopenCase(String caseId, String deploymentId, String caseDefinitionId, Map<String, Object> data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));
        if (pi != null) {
            throw new CaseActiveException("Case with id " + caseId + " is still active and cannot be reopened"); 
        }
        logger.debug("About to reopen case {} by starting process instance {} from deployment {} with additional data {}", 
                caseId, caseDefinitionId, deploymentId, data);
        
        processService.execute(deploymentId, CaseContext.get(caseId), new ReopenCaseCommand(caseId, deploymentId, caseDefinitionId, data, processService));
        
    }


    
    /*
     * Dynamic operations on a case
     */
    

    @Override
    public void addDynamicTask(String caseId, TaskSpecification taskSpecification) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicTaskCommand(caseId, taskSpecification.getNodeType(), pi.getId(), taskSpecification.getParameters()));
    }
    
    @Override
    public void addDynamicTask(Long processInstanceId, TaskSpecification taskSpecification) throws ProcessInstanceNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicTaskCommand(caseId, taskSpecification.getNodeType(), pi.getId(), taskSpecification.getParameters()));
    }

    @Override
    public void addDynamicTaskToStage(String caseId, String stageId, TaskSpecification taskSpecification) throws CaseNotFoundException, StageNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + pi.getId() + " or it's not active anymore");
        }
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicTaskToStageCommand(caseId, taskSpecification.getNodeType(), pi.getId(), stageId, taskSpecification.getParameters()));
    }
    

    @Override
    public void addDynamicTaskToStage(Long processInstanceId, String stageId, TaskSpecification taskSpecification) throws CaseNotFoundException, StageNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicTaskToStageCommand(caseId, taskSpecification.getNodeType(), pi.getId(), stageId, taskSpecification.getParameters()));        
    }
    

    @Override
    public Long addDynamicSubprocess(String caseId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + pi.getId() + " or it's not active anymore");
        }
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicProcessCommand(caseId, pi.getId(), processId, parameters));
    }

    @Override
    public Long addDynamicSubprocess(Long processInstanceId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        
        String caseId = pi.getCorrelationKey();
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicProcessCommand(caseId, pi.getId(), processId, parameters));
    }
    
    @Override
    public Long addDynamicSubprocessToStage(String caseId, String stageId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDynamicProcessToStageCommand(caseId, pi.getId(), stageId, processId, parameters));
    }

    @Override
    public Long addDynamicSubprocessToStage(Long processInstanceId, String stageId, String processId, Map<String, Object> parameters) throws CaseNotFoundException {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new ProcessInstanceNotFoundException("No process instance found with id " + processInstanceId + " or it's not active anymore");
        }
        String caseId = pi.getCorrelationKey();
        return processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(processInstanceId), new AddDynamicProcessToStageCommand(caseId, pi.getId(), stageId, processId, parameters));
    }
    
    @Override
    public void triggerAdHocFragment(String caseId, String fragmentName, Object data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        triggerAdHocFragment(pi.getId(), fragmentName, data);
    }

    @Override
    public void triggerAdHocFragment(Long processInstanceId, String fragmentName, Object data) throws CaseNotFoundException {
        
        processService.signalProcessInstance(processInstanceId, fragmentName, data);
    }

    /*
     * Case file data methods
     */

    @Override
    public void addDataToCaseFile(String caseId, String name, Object value) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(name, value);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDataCaseFileInstanceCommand(parameters));
        
    }

    @Override
    public void addDataToCaseFile(String caseId, Map<String, Object> data) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new AddDataCaseFileInstanceCommand(data));
    }
    
    @Override
    public void removeDataFromCaseFile(String caseId, String name) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new RemoveDataCaseFileInstanceCommand(Arrays.asList(name)));
        
    }

    @Override
    public void removeDataFromCaseFile(String caseId, List<String> variableNames) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new RemoveDataCaseFileInstanceCommand(variableNames));
    }

    /*
     * Case role assignment methods
     */

    @Override
    public void assignToCaseRole(String caseId, String role, OrganizationalEntity entity) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new ModifyRoleAssignmentCommand(role, entity, true));
    }

    @Override
    public void removeFromCaseRole(String caseId, String role, OrganizationalEntity entity) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new ModifyRoleAssignmentCommand(role, entity, false));
    }

    @Override
    public Collection<CaseRoleInstance> getCaseRoleAssignments(String caseId) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, pi);
        
        return ((CaseFileInstanceImpl)caseFile).getAssignments();
    }

    
    /*
     * Case comments methods
     */
    

    @Override
    public Collection<CommentInstance> getCaseComments(String caseId, QueryContext queryContext) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        
        CaseFileInstance caseFile = internalGetCaseFileInstance(caseId, pi);
        
        return ((CaseFileInstanceImpl)caseFile).getComments();
    }

    @Override
    public Collection<CommentInstance> getCaseComments(String caseId, CommentSortBy sortBy, QueryContext queryContext) throws CaseNotFoundException {
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
    public void addCaseComment(String caseId, String author, String comment) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(author, comment));
    }
    
    @Override
    public void updateCaseComment(String caseId, String commentId, String author, String text) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(commentId, author, text));
        
    }

    @Override
    public void removeCaseComment(String caseId, String commentId) throws CaseNotFoundException {
        ProcessInstanceDesc pi = verifyCaseIdExists(caseId);
        processService.execute(pi.getDeploymentId(), ProcessInstanceIdContext.get(pi.getId()), new CaseCommentCommand(commentId));
    }

    /*
     * new instances methods
     */
    
    @Override
    public CaseFileInstance newCaseFileInstance(String deploymentId, String caseDefinition, Map<String, Object> data) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(data);
        caseFile.setupRoles(def.getCaseRoles());
        
        return caseFile;
    }
    
    @Override
    public CaseFileInstance newCaseFileInstance(String deploymentId, String caseDefinition, Map<String, Object> data, Map<String, OrganizationalEntity> rolesAssignment) {
        CaseDefinition def = caseRuntimeDataService.getCase(deploymentId, caseDefinition);
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl(data);
        caseFile.setupRoles(def.getCaseRoles());
        
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
    protected CaseFileInstance internalGetCaseFileInstance(String caseId, ProcessInstanceDesc pi) {
        logger.debug("Retrieving case file from working memory for case " + caseId);
        Collection<CaseFileInstance> caseFiles = (Collection<CaseFileInstance>) processService.execute(pi.getDeploymentId(), CaseContext.get(caseId), commandsFactory.newGetObjects(new ClassObjectFilter(CaseFileInstance.class)));
        if (caseFiles.size() == 0) {
            throw new CaseNotFoundException("Case with id " + caseId + " was not found");
        } else if (caseFiles.size() == 1) {
            CaseFileInstance caseFile =  caseFiles.iterator().next();
            logger.debug("Single case file {} found in working memory", caseFile);
            return caseFile;
        } 
        logger.warn("Multiple case files found in working memory (most likely not using PER_CASE strategy), trying to filter out...");
        CaseFileInstance caseFile = caseFiles.stream()
                .filter(cf -> cf.getCaseId().equals(caseId))
                .findFirst()
                .orElse(null);
        logger.warn("Case file {} after filtering {}", caseFile, (caseFile == null?"not found" : "found"));
        
        return caseFile;
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


}
