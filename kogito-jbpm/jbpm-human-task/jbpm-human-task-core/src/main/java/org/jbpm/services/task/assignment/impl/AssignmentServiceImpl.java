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

package org.jbpm.services.task.assignment.impl;

import org.jbpm.services.task.assignment.AssignmentService;
import org.jbpm.services.task.assignment.AssignmentServiceRegistry;
import org.jbpm.services.task.assignment.impl.strategy.PotentialOwnerBusynessAssignmentStrategy;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.assignment.Assignment;
import org.kie.internal.task.api.assignment.AssignmentStrategy;
import org.kie.internal.task.api.model.InternalTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssignmentServiceImpl implements AssignmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AssignmentServiceImpl.class);
    
    private static final String ENABLED_PROPERTY = "org.jbpm.task.assignment.enabled";
    
    private boolean enabled = Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false"));

    private AssignmentStrategy strategy;
    private AssignmentServiceRegistry registry = AssignmentServiceRegistry.get();
    private TaskModelFactory taskModelFactory = TaskModelProvider.getFactory();
    
    public AssignmentServiceImpl() {
        this.strategy = registry.getStrategy(System.getProperty("org.jbpm.task.assignment.strategy", PotentialOwnerBusynessAssignmentStrategy.IDENTIFIER));        
    }
    
    public AssignmentServiceImpl(AssignmentStrategy strategy) {        
        this.strategy = strategy;
    }
    
    @Override
    public void assignTask(Task task, TaskContext context) {
        
        assignTask(task, context, null);
    }    

    @Override
    public void assignTask(Task task, TaskContext context, String excludedUser) {
        if (!isEnabled()) {
            logger.debug("AssignmentService is not enabled - to enable it set system property '" + ENABLED_PROPERTY + "' to true");
            return;
        }
 
        Assignment assignTo = this.strategy.apply(task, context, excludedUser);
        if (assignTo == null || assignTo.getUser() == null) {
            logger.warn("Strategy {} did not return any assignment for task {}", strategy, task);
            return;
        }
        logger.debug("Actual owner returned by strategy {} is {} for task {}", strategy, assignTo, task);
        User actualOwner = taskModelFactory.newUser(assignTo.getUser());
        
        ((InternalTaskData) task.getTaskData()).setActualOwner(actualOwner);
        ((InternalTaskData) task.getTaskData()).setStatus(Status.Reserved);
    }

    @Override
    public void onTaskDone(Task task, TaskContext context) {
        if (!isEnabled()) {
            logger.debug("AssignmentService is not enabled - to enable it set system property '" + ENABLED_PROPERTY + "' to true");
            return;
        }
        
        this.strategy.taskDone(task, context);
        logger.debug("Assignment strategy notified about task {} being done", task);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
