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

import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalTaskData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RemoveTaskDataCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;

    private boolean input;
    private List<String> variableNames;

    public RemoveTaskDataCommand(String userId, long taskId, List<String> variableNames, boolean input) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.variableNames = variableNames;
        this.input = input;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        TaskEventSupport taskEventSupport = context.getTaskEventSupport();
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        
        Task task = persistenceContext.findTask(taskId);
        // security check
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }
        
        long contentId = task.getTaskData().getDocumentContentId();
        if (!input) {
            contentId = task.getTaskData().getOutputContentId();
            
        }
        Content outputContent = persistenceContext.findContent(contentId);
        Map<String, Object> initialContent = new HashMap<>();
        Map<String, Object> mergedContent = new HashMap<>();
        
        if (outputContent != null) {             
            ContentMarshallerContext mcontext = context.getTaskContentService().getMarshallerContext(task);
            Object unmarshalledObject = ContentMarshallerHelper.unmarshall(outputContent.getContent(), mcontext.getEnvironment(), mcontext.getClassloader());
            if(unmarshalledObject != null && unmarshalledObject instanceof Map){                
                mergedContent.putAll(((Map<String, Object>)unmarshalledObject));
                // set initial content for the sake of listeners
                initialContent.putAll(mergedContent);
                
                variableNames.forEach(name -> mergedContent.remove(name));
            }
            ContentData outputContentData = ContentMarshallerHelper.marshal(task, mergedContent, mcontext.getEnvironment());
            ((InternalContent)outputContent).setContent(outputContentData.getContent());
            persistenceContext.persistContent(outputContent);
        }
        if (input) {
            taskEventSupport.fireBeforeTaskInputVariablesChanged(task, context, initialContent);
            ((InternalTaskData)task.getTaskData()).setTaskInputVariables(mergedContent);
            taskEventSupport.fireAfterTaskInputVariablesChanged(task, context, mergedContent);
        } else {
            taskEventSupport.fireBeforeTaskOutputVariablesChanged(task, context, initialContent);
            ((InternalTaskData)task.getTaskData()).setTaskOutputVariables(mergedContent);
            taskEventSupport.fireAfterTaskOutputVariablesChanged(task, context, mergedContent);
        }
                
        
        return null;
    }

}
