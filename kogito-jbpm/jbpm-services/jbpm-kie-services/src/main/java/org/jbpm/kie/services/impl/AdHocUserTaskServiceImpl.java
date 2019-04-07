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
package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.AdHocUserTaskService;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdHocUserTaskServiceImpl implements AdHocUserTaskService, VariablesAware {

    private static final Logger logger = LoggerFactory.getLogger(AdHocUserTaskServiceImpl.class);

    private InternalTaskService nonProcessScopedTaskService;

    public void setNonProcessScopedTaskService(InternalTaskService nonProcessScopedTaskService) {
        this.nonProcessScopedTaskService = nonProcessScopedTaskService;
    }

    protected InternalTaskService getInternalTaskService() {
        return this.nonProcessScopedTaskService;
    }

    @Override
    public long addTask(
            final String taskName,
            int priority,
            Date dueDate, final List<String> users, List<String> groups, String identity,
            String taskformName, String deploymentId, Long processInstanceId, Map<String, Object> params,
            boolean autoStart, boolean autoClaim) {
        TaskFluent taskFluent = new TaskFluent().setName(taskName)
                .setPriority(priority)
                .setDueDate(dueDate)
                .setFormName(taskformName);
        if (deploymentId != null && !deploymentId.equals("")) {
            taskFluent.setDeploymentID(deploymentId);
        } else {
            taskFluent.setDeploymentID(null);
        }
        if (processInstanceId > 0) {
            taskFluent.setProcessInstanceId(processInstanceId);
        }

        for (String user : users) {
            taskFluent.addPotentialUser(user);
        }
        for (String group : groups) {
            taskFluent.addPotentialGroup(group);
        }
        taskFluent.setAdminUser("Administrator");
        taskFluent.setAdminGroup("Administrators");
        Task task = taskFluent.getTask();
        if(params == null){
            params = new HashMap<String, Object>();
        }
        long taskId = nonProcessScopedTaskService.addTask(taskFluent.getTask(), params);
        if (autoStart) {
            nonProcessScopedTaskService.start(taskId, identity);
        }
        if (autoClaim) {
            nonProcessScopedTaskService.claim(taskId, identity);
        }

        return taskId;
    }

    @Override
    public void updateTask(long taskId, int priority, String taskDescription,
            Date dueDate) {
        nonProcessScopedTaskService.setPriority(taskId, priority);
        if (taskDescription != null) {
            InternalI18NText text = (InternalI18NText) TaskModelProvider.getFactory().newI18NText();
            text.setLanguage("en-UK");
            text.setText(taskDescription);
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(text);
            nonProcessScopedTaskService.setDescriptions(taskId, names);
        }
        if (dueDate != null) {
            nonProcessScopedTaskService.setExpirationDate(taskId, dueDate);
        }
    }

    @Override
    public <T> T process(T variables, ClassLoader cl) {
        // do nothing here as there is no need to process variables
        return variables;
    }

    @Override
    public long addTask(Task task, Map<String, Object> params) {
        return nonProcessScopedTaskService.addTask(task, params);
    }
}
