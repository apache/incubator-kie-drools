/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service.processing;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.model.processing.AttributesProcessor;
import org.kie.kogito.taskassigning.model.processing.TaskAttributesProcessor;
import org.kie.kogito.taskassigning.model.processing.TaskInfo;
import org.kie.kogito.taskassigning.model.processing.UserAttributesProcessor;
import org.kie.kogito.taskassigning.service.TaskAssigningException;
import org.kie.kogito.taskassigning.user.service.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AttributesProcessorRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributesProcessorRegistry.class);

    private final List<UserAttributesProcessor> userAttributesProcessors;
    private final List<TaskAttributesProcessor> taskAttributesProcessors;

    @Inject
    public AttributesProcessorRegistry(Instance<AttributesProcessor<?>> processorsInstance) {
        userAttributesProcessors = new ArrayList<>();
        taskAttributesProcessors = new ArrayList<>();
        for (AttributesProcessor<?> processor : processorsInstance) {
            if (processor.isEnabled()) {
                if (processor instanceof UserAttributesProcessor) {
                    userAttributesProcessors.add((UserAttributesProcessor) processor);
                } else if (processor instanceof TaskAttributesProcessor) {
                    taskAttributesProcessors.add((TaskAttributesProcessor) processor);
                } else {
                    throw new IllegalArgumentException("Unexpected processor implementation: " + processor.getClass() +
                            ", valid implementations must implement one of the following classes UserAttributesProcessor or TaskAttributesProcessor");
                }
            } else {
                LOGGER.info("Attributes processor {} has been disabled.", processor.getClass());
            }
        }
        userAttributesProcessors.sort(Comparator.comparingInt(UserAttributesProcessor::getPriority));
        taskAttributesProcessors.sort(Comparator.comparingInt(TaskAttributesProcessor::getPriority));
    }

    public void applyAttributesProcessor(Task task, Map<String, Object> targetAttributes) {
        TaskInfo taskInfo = wrapTask(task);
        for (TaskAttributesProcessor processor : taskAttributesProcessors) {
            try {
                processor.process(taskInfo, targetAttributes);
            } catch (Exception e) {
                String msg = String.format("An error was produced during a task processor execution" +
                        ", processor class: %s, taskId: %s, processInstanceId: %s, processId: %s, error: %s", processor.getClass().getName(),
                        taskInfo.getTaskId(), taskInfo.getProcessInstanceId(), taskInfo.getProcessId(), e.getMessage());
                throw new TaskAssigningException(msg);
            }
        }
    }

    public void applyAttributesProcessor(User user, Map<String, Object> targetAttributes) {
        for (UserAttributesProcessor processor : userAttributesProcessors) {
            try {
                processor.process(user, targetAttributes);
            } catch (Exception e) {
                String msg = String.format("An error was produced during a user processor execution" +
                        ", processor class: %s, userId: %s, error: %s", processor.getClass().getName(), user.getId(), e.getMessage());
                throw new TaskAssigningException(msg);
            }
        }
    }

    private static TaskInfo wrapTask(Task task) {
        return new TaskInfo() {
            @Override
            public String getTaskId() {
                return task.getId();
            }

            @Override
            public String getName() {
                return task.getName();
            }

            @Override
            public String getDescription() {
                return task.getDescription();
            }

            @Override
            public String getReferenceName() {
                return task.getReferenceName();
            }

            @Override
            public String getPriority() {
                return task.getPriority();
            }

            @Override
            public String getProcessInstanceId() {
                return task.getProcessInstanceId();
            }

            @Override
            public String getProcessId() {
                return task.getProcessId();
            }

            @Override
            public String getRootProcessInstanceId() {
                return task.getRootProcessInstanceId();
            }

            @Override
            public String getRootProcessId() {
                return task.getRootProcessId();
            }

            @Override
            public Set<String> getPotentialUsers() {
                return task.getPotentialUsers();
            }

            @Override
            public Set<String> getPotentialGroups() {
                return task.getPotentialGroups();
            }

            @Override
            public Set<String> getAdminUsers() {
                return task.getAdminUsers();
            }

            @Override
            public Set<String> getAdminGroups() {
                return task.getAdminGroups();
            }

            @Override
            public Set<String> getExcludedUsers() {
                return task.getExcludedUsers();
            }

            @Override
            public ZonedDateTime getStarted() {
                return task.getStarted();
            }

            @Override
            public Map<String, Object> getInputs() {
                return task.getInputData();
            }

            @Override
            public String getEndpoint() {
                return task.getEndpoint();
            }
        };
    }
}
