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

package org.jbpm.kie.services.impl.admin.commands;

import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UpdateTaskCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;

    private UserTaskInstanceDesc userTask;
    
    private Map<String, Object> inputs;
    private Map<String, Object> outputs;

    public UpdateTaskCommand(Long taskId, String userId, UserTaskInstanceDesc userTask, Map<String, Object> inputs, Map<String, Object> outputs) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.userTask = userTask;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        TaskEventSupport taskEventSupport = context.getTaskEventSupport();
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        
        Task task = persistenceContext.findTask(taskId);
        // security check
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context) 
                && !isOwner(userId, task.getPeopleAssignments().getPotentialOwners(), task.getTaskData().getActualOwner(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin or potential owner of task " + taskId);
        }
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        
        // process task meta data
        if (userTask.getFormName() != null) {
            ((InternalTask) task).setFormName(userTask.getFormName());
        }
        if (userTask.getName() != null) {
            ((InternalTask) task).setName(userTask.getName());
        }
        if (userTask.getDescription() != null) {
            ((InternalTask) task).setDescription(userTask.getDescription());
        }
        if (userTask.getPriority() != null) {
            ((InternalTask) task).setPriority(userTask.getPriority());
        }
        if (userTask.getDueDate() != null) {
            ((InternalTaskData) task.getTaskData()).setExpirationTime(userTask.getDueDate());
        }
        
        // process task inputs
        long inputContentId = task.getTaskData().getDocumentContentId();
        Content inputContent = persistenceContext.findContent(inputContentId);

        Map<String, Object> mergedContent = inputs;
        
        if (inputs != null) {
            if (inputContent == null) { 
                ContentMarshallerContext mcontext = context.getTaskContentService().getMarshallerContext(task);
                ContentData outputContentData = ContentMarshallerHelper.marshal(task, inputs, mcontext.getEnvironment());
                Content content = TaskModelProvider.getFactory().newContent();
                ((InternalContent) content).setContent(outputContentData.getContent());
                persistenceContext.persistContent(content);
                
                ((InternalTaskData) task.getTaskData()).setOutput(content.getId(), outputContentData);
            } else {
                ContentMarshallerContext mcontext = context.getTaskContentService().getMarshallerContext(task);
                Object unmarshalledObject = ContentMarshallerHelper.unmarshall(inputContent.getContent(), mcontext.getEnvironment(), mcontext.getClassloader());
                if(unmarshalledObject != null && unmarshalledObject instanceof Map){
                    ((Map<String, Object>)unmarshalledObject).putAll(inputs);
                    mergedContent = ((Map<String, Object>)unmarshalledObject);
                }
                ContentData outputContentData = ContentMarshallerHelper.marshal(task, unmarshalledObject, mcontext.getEnvironment());
                ((InternalContent)inputContent).setContent(outputContentData.getContent());
                persistenceContext.persistContent(inputContent);
            }
            ((InternalTaskData)task.getTaskData()).setTaskInputVariables(mergedContent);
        }
        
        if (outputs != null) {
            // process task outputs
            context.getTaskContentService().addOutputContent(taskId, outputs);
        }
        
        persistenceContext.updateTask(task);
        // finally trigger event support after the updates
        taskEventSupport.fireAfterTaskUpdated(task, context);
        
        return null;
    }
    
    protected boolean isOwner(String userId, List<OrganizationalEntity> potentialOwners, OrganizationalEntity actualOwner, TaskContext context) {
        List<String> usersGroup = new ArrayList<>(context.getUserGroupCallback().getGroupsForUser(userId));
        usersGroup.add(userId);
        
        if (actualOwner != null) {
            boolean isOwner = userId.equals(actualOwner.getId());
            if (isOwner) {
                return true;
            }
        }
        
        return potentialOwners.stream().anyMatch(oe -> usersGroup.contains(oe.getId()));
        
    }

}
