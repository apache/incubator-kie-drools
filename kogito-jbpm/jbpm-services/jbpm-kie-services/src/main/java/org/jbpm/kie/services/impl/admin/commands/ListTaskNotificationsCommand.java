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

import org.jbpm.kie.services.impl.admin.TaskNotificationImpl;
import org.jbpm.services.api.admin.TaskNotification;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ListTaskNotificationsCommand extends UserGroupCallbackTaskCommand<List<TaskNotification>> {

    private static final long serialVersionUID = -1856489382099976731L;
    private static final Logger logger = LoggerFactory.getLogger(ListTaskNotificationsCommand.class);
    
    private boolean activeOnly;
    private TaskModelFactory factory = TaskModelProvider.getFactory();
    
    public ListTaskNotificationsCommand(String userId, long taskId, boolean activeOnly) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.activeOnly = activeOnly;
    }

    @Override
    public List<TaskNotification> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Language lang = factory.newLanguage();
        lang.setMapkey("en-UK");
        
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);  
        
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }        
        Deadlines deadlines = ((InternalTask)task).getDeadlines();              
        
        
        List<TaskNotification> notificationsNotStarted = deadlines.getStartDeadlines().stream().
        filter(d -> !d.getEscalations().isEmpty() && !d.getEscalations().get(0).getNotifications().isEmpty())
        .map( d -> {
            Notification n = d.getEscalations().get(0).getNotifications().get(0);
            EmailNotificationHeader email = ((EmailNotification) n).getEmailHeaders().get(lang);
            return new TaskNotificationImpl(d.getId(), get(n.getNames()), email.getSubject(), email.getBody(), d.getDate(), n.getRecipients(), n.getBusinessAdministrators(), !d.isEscalated());
        })
        .collect(Collectors.toList());
        
        List<TaskNotification> notificationsNotCompleted = deadlines.getEndDeadlines().stream().
                filter(d -> !d.getEscalations().isEmpty() && !d.getEscalations().get(0).getNotifications().isEmpty())
                .map( d -> {
                    Notification n = d.getEscalations().get(0).getNotifications().get(0);
                    EmailNotificationHeader email = ((EmailNotification) n).getEmailHeaders().get(lang);
                    return new TaskNotificationImpl(d.getId(), get(n.getNames()), email.getSubject(), email.getBody(), d.getDate(), n.getRecipients(), n.getBusinessAdministrators(), !d.isEscalated());
                })
                .collect(Collectors.toList());
        
        List<TaskNotification> result = new ArrayList<>();
        result.addAll(notificationsNotStarted);
        result.addAll(notificationsNotCompleted);
        if (activeOnly) {
            logger.debug("Removing already completed deadlines from the result");
            result = result.stream().filter(t -> t.isActive()).collect(Collectors.toList());
        }
        return result;
    }
    
    protected String get(List<I18NText> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        
        return list.get(0).getText();
    }

}
