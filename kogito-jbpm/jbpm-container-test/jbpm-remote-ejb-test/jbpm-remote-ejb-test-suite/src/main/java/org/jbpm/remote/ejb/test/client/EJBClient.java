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

package org.jbpm.remote.ejb.test.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.client.ClientServiceFactory;
import org.jbpm.services.ejb.client.ServiceFactoryProvider;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJBClient {

    private static final String APPLICATION = "ejb-services-app";

    private final Logger log = LoggerFactory.getLogger(EJBClient.class);
    
    private final String deploymentId;

    private final DeploymentServiceEJBRemote deploymentService;
    private final ProcessServiceEJBRemote processService;
    private final RuntimeDataServiceEJBRemote runtimeDataService;
    private final DefinitionServiceEJBRemote definitionService;
    private final UserTaskServiceEJBRemote userTaskService;

    public EJBClient(String deploymentId) throws NamingException {
        this.deploymentId = deploymentId;
        
        ClientServiceFactory factory = ServiceFactoryProvider.getProvider("JBoss");
        
        deploymentService = factory.getService(APPLICATION, DeploymentServiceEJBRemote.class);
        processService = factory.getService(APPLICATION, ProcessServiceEJBRemote.class);
        runtimeDataService = factory.getService(APPLICATION, RuntimeDataServiceEJBRemote.class);
        definitionService = factory.getService(APPLICATION, DefinitionServiceEJBRemote.class);
        userTaskService = factory.getService(APPLICATION, UserTaskServiceEJBRemote.class);
        
        log.debug("Deployment Service:   " + deploymentService);
        log.debug("Process Service:      " + processService);
        log.debug("Runtime Data Service: " + runtimeDataService);
        log.debug("Definition Service:   " + definitionService);
        log.debug("User Task Service:    " + userTaskService);
    }

    private <T> List<T> toList(Collection<T> c) {
        List<T> l = new ArrayList<>();
        if (c != null) {
            l.addAll(c);
        }
        return l;
    }
    
    public DeploymentServiceEJBRemote getDeploymentService() {
        return deploymentService;
    }
    
    public DefinitionServiceEJBRemote getDefinitionService() {
        return definitionService;
    }
    
    public RuntimeDataServiceEJBRemote getRuntimeDataService() {
        return runtimeDataService;
    }
    
    public ProcessServiceEJBRemote getProcessService() {
        return processService;
    }
    
    public UserTaskServiceEJBRemote getUserTaskService() {
        return userTaskService;
    }
    
    // *********** UserTaskServiceEJBRemote

    public void activate(Long taskId, String userId) {
        userTaskService.activate(taskId, userId);
    }

    public void claim(Long taskId, String userId) {
        userTaskService.claim(taskId, userId);
    }

    public void complete(Long taskId, String userId, Map<String, Object> data) {
        userTaskService.complete(taskId, userId, data);
    }

    public void delegate(Long taskId, String userId, String targetUserId) {
        userTaskService.delegate(taskId, userId, targetUserId);
    }

    public void exit(Long taskId, String userId) {
        userTaskService.exit(taskId, userId);
    }

    public void fail(Long taskId, String userId, Map<String, Object> faultData) {
        userTaskService.fail(taskId, userId, faultData);
    }

    public void forward(Long taskId, String userId, String targetEntityId) {
        userTaskService.forward(taskId, userId, targetEntityId);
    }

    public Long addComment(Long taskId, String text, String addedBy, Date addedOn) {
        return userTaskService.addComment(taskId, text, addedBy, addedOn);
    }

    public void deleteComment(Long taskId, Long commentId) {
        userTaskService.deleteComment(taskId, commentId);
    }

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return userTaskService.getCommentsByTaskId(taskId);
    }

    public Comment getCommentById(Long taskId, Long commentId) {
        return userTaskService.getCommentById(taskId, commentId);
    }

    public Long addAttachment(Long taskId, String userId, String name, Object attachment) {
        return userTaskService.addAttachment(taskId, userId, name, attachment);
    }

    public void deleteAttachment(Long taskId, Long attachmentId) {
        userTaskService.deleteAttachment(taskId, attachmentId);
    }

    public Attachment getAttachmentById(Long taskId, Long attachmentId) {
        return userTaskService.getAttachmentById(taskId, attachmentId);
    }

    public List<Attachment> getAttachmentsByTaskId(Long taskId) {
        return userTaskService.getAttachmentsByTaskId(taskId);
    }

    public Task getTask(Long taskId) {
        return userTaskService.getTask(taskId);
    }
    
    public void release(Long taskId, String userId) {
        userTaskService.release(taskId, userId);
    }

    public void resume(Long taskId, String userId) {
        userTaskService.resume(taskId, userId);
    }

    public void skip(Long taskId, String userId) {
        userTaskService.skip(taskId, userId);
    }

    public void start(Long taskId, String userId) {
        userTaskService.start(taskId, userId);
    }

    public void stop(Long taskId, String userId) {
        userTaskService.stop(taskId, userId);
    }

    public void suspend(Long taskId, String userId) {
        userTaskService.suspend(taskId, userId);
    }

    public void nominate(Long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        userTaskService.nominate(taskId, userId, potentialOwners);
    }

    public Map<String, Object> getTaskOutputContentByTaskId(Long l) {
        return userTaskService.getTaskOutputContentByTaskId(l);
    }

    public Map<String, Object> getTaskInputContentByTaskId(Long l) {
        return userTaskService.getTaskInputContentByTaskId(l);
    }

    // *********** ProcessServiceEJBRemote
    
    public <T> T execute(Command<T> command) {
        return processService.execute(deploymentId, command);
    }
    
    public Long startProcessSimple(String processId) {
        return processService.startProcess(deploymentId, processId);
    }

    public Long startProcess(String processId) {
        Long pid = startProcessSimple(processId);
        log.info("Started process of type '" + processId + "' with id '" + pid + "'");
        return pid;
    }

    public Long startProcess(String processId, Map<String, Object> parameters) {
        Long pid = processService.startProcess(deploymentId, processId, parameters);
        log.info("Started process of type '" + processId + "' with id '" + pid + "' with parameters");
        return pid;
    }

    /**
     * Be sure to use this method for processes containing a save point. Otherwise you risk that
     * org.kie.internal.runtime.manager.SessionNotFoundException raises as the method queries for a process instance
     * that could be already finished.
     * @param processId process definition ID
     * @return instance of a running process
     * */
    public ProcessInstance startAndGetProcess(String processId) {
        return getProcessInstance(startProcess(processId));
    }

    /**
     * Be sure to use this method for processes containing a save point. Otherwise you risk that
     * org.kie.internal.runtime.manager.SessionNotFoundException raises as the method queries for a process instance
     * that could be already finished.
     * @param processId process definition ID
     * @param parameters process instance parameters
     * @return instance of a running process
     * */
    public ProcessInstance startAndGetProcess(String processId, Map<String, Object> parameters) {
        return getProcessInstance(startProcess(processId, parameters));
    }

    public void signalProcessInstance(Long processInstanceId, String type, Object event) {
        processService.signalProcessInstance(processInstanceId, type, event);
    }

    public ProcessInstance getProcessInstance(Long processInstanceId) {
        return processService.getProcessInstance(processInstanceId);
    }

    public void abortProcessInstance(Long processInstanceId) {
        processService.abortProcessInstance(processInstanceId);
    }
            
    public void completeWorkItem(Long workItemId) {
        completeWorkItem(workItemId, null);
    }
 
    public void completeWorkItem(Long workItemId, Map<String, Object> results) {
        processService.completeWorkItem(workItemId, results);
    }
    
    public void abortWorkItem(Long workItemId) {
        processService.abortWorkItem(workItemId);
    }
    
    public List<String> getAvailableSignals(Long processInstanceId) {
        return (List<String>) processService.getAvailableSignals(processInstanceId);
    }
    
    public void setProcessVariable(Long processInstanceId, String variableName, Object variableValue) {
        processService.setProcessVariable(processInstanceId, variableName, variableValue);
    }
    
    public Object getProcessVariable(Long processInstanceId, String variableName) {
        return processService.getProcessInstanceVariable(processInstanceId, variableName);
    }

    public void setProcessVariables(Long processInstanceId, Map<String, Object> variableMap) {
        processService.setProcessVariables(processInstanceId, variableMap);
    }

    public Map<String, Object> getProcessInstanceVariables(Long processInstanceId) {
        return processService.getProcessInstanceVariables(processInstanceId);
    }

    public List<WorkItem> getWorkItemByProcessInstance(Long processInstanceId) {
        return processService.getWorkItemByProcessInstance(processInstanceId);
    }

    // *********** DeploymentServiceEJBRemote
    
    public void deploy(String groupId, String artefactId, String version) {
        deploymentService.deploy(groupId, artefactId, version);
    }

    public void deploy(String groupId, String artefactId, String version, String kbaseName, String ksessionName) {
        deploymentService.deploy(groupId, artefactId, version, kbaseName, ksessionName);
    }

    public void deploy(String groupId, String artefactId, String version, String kbaseName, String ksessionName, String strategy) {
        deploymentService.deploy(groupId, artefactId, version, kbaseName, ksessionName, strategy);
    }

    public void undeploy(String deploymentId) {
        deploymentService.undeploy(deploymentId);
    }

    public boolean isDeployed(String deploymentId) {
        return deploymentService.isDeployed(deploymentId);
    }
    
    // *********** DefinitionServiceEJBRemote
    
    public ProcessDefinition buildProcessDefinition(String deploymentId, String bpmn2Content, KieContainer kcontainer, boolean cache) throws IllegalArgumentException {
        return definitionService.buildProcessDefinition(deploymentId, bpmn2Content, kcontainer, cache);
    }

    public ProcessDefinition getProcessDefinition(String deploymentId, String processId) {
        return definitionService.getProcessDefinition(deploymentId, processId);
    }

    public Collection<String> getReusableSubProcesses(String deploymentId, String processId) {
        return definitionService.getReusableSubProcesses(deploymentId, processId);
    }

    public Map<String, String> getProcessVariables(String deploymentId, String processId) {
        return definitionService.getProcessVariables(deploymentId, processId);
    }

    public Map<String, String> getServiceTasks(String deploymentId, String processId) {
        return definitionService.getServiceTasks(deploymentId, processId);
    }

    public Map<String, Collection<String>> getAssociatedEntities(String deploymentId, String processId) {
        return definitionService.getAssociatedEntities(deploymentId, processId);
    }

    public Collection<UserTaskDefinition> getTasksDefinitions(String deploymentId, String processId) {
        return definitionService.getTasksDefinitions(deploymentId, processId);
    }

    public Map<String, String> getTaskInputMappings(String deploymentId, String processId, String taskName) {
        return definitionService.getTaskInputMappings(deploymentId, processId, taskName);
    }

    public Map<String, String> getTaskOutputMappings(String deploymentId, String processId, String taskName) {
        return definitionService.getTaskOutputMappings(deploymentId, processId, taskName);
    }
    
    // *********** RuntimeDataServiceEJBRemote
    
    public List<ProcessInstanceDesc> getProcessInstances() {
        return toList(runtimeDataService.getProcessInstances(new QueryContext()));
    }
    
    public List<ProcessInstanceDesc> getProcessInstances(QueryContext context) {
        return toList(runtimeDataService.getProcessInstances(context));
    }

    public List<ProcessInstanceDesc> getProcessInstances(List<Integer> list, String string, QueryContext context) {
        return toList(runtimeDataService.getProcessInstances(list, string, context));
    }

    public List<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String indicator, QueryContext context) {
        return toList(runtimeDataService.getProcessInstancesByProcessId(states, processId, indicator, context));
    }

    public List<ProcessInstanceDesc> getProcessInstancesByProcessName(List<Integer> states, String processName, String indicator, QueryContext context) {
        return toList(runtimeDataService.getProcessInstancesByProcessName(states, processName, indicator, context));
    }

    public List<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states, QueryContext context) {
        return toList(runtimeDataService.getProcessInstancesByDeploymentId(deploymentId, states, context));
    }

    public ProcessInstanceDesc getProcessInstanceById(long processInstanceId) {
        return runtimeDataService.getProcessInstanceById(processInstanceId);
    }

    public List<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefinitionId) {
        return toList(runtimeDataService.getProcessInstancesByProcessDefinition(processDefinitionId, new QueryContext()));
    }
    
    public List<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefinitionId, QueryContext context) {
        return toList(runtimeDataService.getProcessInstancesByProcessDefinition(processDefinitionId, context));
    }
    
    public List<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefinitionId, List<Integer> states, QueryContext context) {
        return toList(runtimeDataService.getProcessInstancesByProcessDefinition(processDefinitionId, states, context));
    }

    public NodeInstanceDesc getNodeInstanceForWorkItem(Long nodeInstanceId) {
        return runtimeDataService.getNodeInstanceForWorkItem(nodeInstanceId);
    }

    public List<NodeInstanceDesc> getProcessInstanceHistoryActive(long l, QueryContext context) {
        return toList(runtimeDataService.getProcessInstanceHistoryActive(l, context));
    }

    public List<NodeInstanceDesc> getProcessInstanceHistoryCompleted(long l, QueryContext context) {
        return toList(runtimeDataService.getProcessInstanceHistoryCompleted(l, context));
    }

    public List<NodeInstanceDesc> getProcessInstanceFullHistory(long processInstanceId) {
        return toList(runtimeDataService.getProcessInstanceFullHistory(processInstanceId, new QueryContext()));
    }
    
    public List<NodeInstanceDesc> getProcessInstanceFullHistory(long processInstanceId, QueryContext context) {
        return toList(runtimeDataService.getProcessInstanceFullHistory(processInstanceId, context));
    }

    public List<NodeInstanceDesc> getProcessInstanceFullHistoryByType(long processId, RuntimeDataService.EntryType type, QueryContext context) {
        return toList(runtimeDataService.getProcessInstanceFullHistoryByType(processId, type, context));
    }

    public List<VariableDesc> getVariablesCurrentState(long processInstanceId) {
        return toList(runtimeDataService.getVariablesCurrentState(processInstanceId));
    }

    public List<VariableDesc> getVariableHistory(long processInstanceId, String variableId) {
        return toList(runtimeDataService.getVariableHistory(processInstanceId, variableId, new QueryContext()));
    }

    public List<VariableDesc> getVariableHistory(long processInstanceId, String variableId, QueryContext context) {
        return toList(runtimeDataService.getVariableHistory(processInstanceId, variableId, context));
    }

    public List<ProcessDefinition> getProcessesByDeploymentId(String deploymentId, QueryContext context) {
        return toList(runtimeDataService.getProcessesByDeploymentId(deploymentId, context));
    }

    public List<ProcessDefinition> getProcessesByFilter(String deploymentId, QueryContext context) {
        return toList(runtimeDataService.getProcessesByFilter(deploymentId, context));
    }

    public List<ProcessDefinition> getProcesses(QueryContext context) {
        return toList(runtimeDataService.getProcesses(context));
    }

    public List<String> getProcessIds(QueryContext context) {
        return toList(runtimeDataService.getProcessIds(deploymentId, context));
    }

    public ProcessDefinition getProcessesByDeploymentIdProcessId(String processId) {
        return runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId) {
        return runtimeDataService.getTasksAssignedAsPotentialOwner(userId, new QueryFilter());
    }
    
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsPotentialOwner(userId, filter);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsPotentialOwner(userId, groupIds, filter);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsPotentialOwner(userId, groupIds, status, filter);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date from, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, status, from, filter);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus(userId, status, filter);
    }
    
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date from, QueryFilter filter) {
        return runtimeDataService.getTasksOwnedByExpirationDateOptional(userId, status, from, filter);
    }

    public List<Long> getTasksByProcessInstanceId(Long processInstanceId) {
        return runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    }

    public List<TaskSummary> getTasksByStatusByProcessInstanceId(Long processInstanceId, List<Status> status, QueryFilter filter) {
        return runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, status, filter);
    }
    
    public UserTaskInstanceDesc getTaskByWorkItemId(Long workItemId) {
        return runtimeDataService.getTaskByWorkItemId(workItemId);
    }

    public UserTaskInstanceDesc getTaskById(Long taskId) {
        return runtimeDataService.getTaskById(taskId);
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, QueryFilter filter) {
        return runtimeDataService.getTasksAssignedAsBusinessAdministrator(userId, filter);
    }

    public List<TaskSummary> getTasksOwned(String userId, QueryFilter filter) {
        return runtimeDataService.getTasksOwned(userId, filter);
    }

    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, QueryFilter filter) {
        return runtimeDataService.getTasksOwnedByStatus(userId, status, filter);
    }

    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status) {
        return runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, status, new QueryFilter());
    }
    
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, QueryFilter filter) {
        return runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, status, filter);
    }

    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        return runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    }
    
}