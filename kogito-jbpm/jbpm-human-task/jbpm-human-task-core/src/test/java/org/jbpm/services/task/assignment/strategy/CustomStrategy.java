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
package org.jbpm.services.task.assignment.strategy;

import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.assignment.Assignment;
import org.kie.internal.task.api.assignment.AssignmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomStrategy implements AssignmentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CustomStrategy.class);
    private static final String IDENTIFIER = "Custom";
    private String assignToUser;

    public CustomStrategy(String assignToUser) {
        this.assignToUser = assignToUser;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Assignment apply(Task task, TaskContext tc, String string) {
        logger.debug("Task {} is assign to user {}.", task.getId(), assignToUser);
        return new Assignment(assignToUser);

    }

    @Override
    public String toString() {
        return "AssignmentStrategy:: " + IDENTIFIER + " This strategy assign all task to user " + assignToUser;
    }

}