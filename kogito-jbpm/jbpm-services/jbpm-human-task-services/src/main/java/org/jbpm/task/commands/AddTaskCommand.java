/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.commands;

import java.util.Map;
import javax.enterprise.util.AnnotationLiteral;
import org.kie.command.Context;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.Task;
import org.jbpm.task.events.AfterTaskAddedEvent;
import org.jbpm.task.events.BeforeTaskAddedEvent;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 * Operation.Start : [ new OperationCommand().{ status = [ Status.Ready ],
 * allowed = [ Allowed.PotentialOwner ], setNewOwnerToUser = true, newStatus =
 * Status.InProgress }, new OperationCommand().{ status = [ Status.Reserved ],
 * allowed = [ Allowed.Owner ], newStatus = Status.InProgress } ], *
 */
@Transactional
public class AddTaskCommand<Long> extends TaskCommand {

    private Task task;
    private Map<String, Object> params;
    private ContentData data;

    public AddTaskCommand(Task task, Map<String, Object> params) {
        this.task = task;
        this.params = params;
    }

    public AddTaskCommand(Task task, ContentData data) {
        this.task = task;
        this.data = data;
    }

    public Long execute(Context cntxt) {
        
        TaskContext context = (TaskContext) cntxt;
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskAddedEvent>() {
        }).fire(task);
        if (params != null) {
            ContentData contentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(contentData.getContent());
            context.getEm().persist(content);
            task.getTaskData().setDocument(content.getId(), contentData);
        }
        if (data != null) {
            Content content = new Content(data.getContent());
            context.getEm().persist(content);
            task.getTaskData().setDocument(content.getId(), data);
        }
        
        context.getEm().persist(task);
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskAddedEvent>() {
        }).fire(task);
        return (Long) task.getId();
    }

    public Task getTask() {
        return task;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public ContentData getData() {
        return data;
    }
    
    
    
    
}
