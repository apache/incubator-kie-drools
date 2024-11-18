/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.usertask.jpa.mapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.usertask.jpa.mapper.json.utils.JSONUtils;
import org.jbpm.usertask.jpa.model.TaskReassignmentTimerEntity;
import org.jbpm.usertask.jpa.model.TaskReassignmentType;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskReassignmentTimerRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Reassignment;

public class TaskReassignmentTimerEntityMapper implements EntityMapper {

    private final TaskReassignmentTimerRepository repository;

    public TaskReassignmentTimerEntityMapper(TaskReassignmentTimerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance instance, UserTaskInstanceEntity userTaskInstanceEntity) {
        if (userTaskInstanceEntity.getReassignmentTimers() == null) {
            userTaskInstanceEntity.setReassignmentTimers(new ArrayList<>());
        }
        DefaultUserTaskInstance userTaskInstance = (DefaultUserTaskInstance) instance;

        List<TaskReassignmentTimerEntity> entities = new ArrayList<>();
        List<String> notStartedReassignments = new ArrayList<>();
        List<String> notCompletedReassignments = new ArrayList<>();

        Iterator<TaskReassignmentTimerEntity> iterator = userTaskInstanceEntity.getReassignmentTimers().iterator();
        while (iterator.hasNext()) {
            TaskReassignmentTimerEntity deadlineEntity = iterator.next();
            switch (deadlineEntity.getType()) {
                case NotCompleted:
                    if (!userTaskInstance.getNotCompletedReassignmentsTimers().keySet().contains(deadlineEntity.getJobId())) {
                        entities.add(deadlineEntity);
                    } else {
                        notCompletedReassignments.add(deadlineEntity.getJobId());
                    }
                    break;
                case NotStarted:
                    if (!userTaskInstance.getNotStartedReassignmentsTimers().keySet().contains(deadlineEntity.getJobId())) {
                        entities.add(deadlineEntity);
                    } else {
                        notStartedReassignments.add(deadlineEntity.getJobId());
                    }
                    break;
            }
        }

        entities.forEach(e -> {
            repository.remove(e);
            userTaskInstanceEntity.getReassignmentTimers().remove(e);
        });
        entities.clear();

        for (Map.Entry<String, Reassignment> timer : userTaskInstance.getNotStartedReassignmentsTimers().entrySet()) {
            if (!notStartedReassignments.contains(timer.getKey())) {
                TaskReassignmentTimerEntity entity = buildEntity(userTaskInstanceEntity, timer.getKey(), timer.getValue(), TaskReassignmentType.NotStarted);
                entities.add(entity);
            }
        }

        for (Map.Entry<String, Reassignment> timer : userTaskInstance.getNotCompletedReassignmentsTimers().entrySet()) {
            if (!notCompletedReassignments.contains(timer.getKey())) {
                TaskReassignmentTimerEntity entity = buildEntity(userTaskInstanceEntity, timer.getKey(), timer.getValue(), TaskReassignmentType.NotCompleted);
                entities.add(entity);
            }
        }
        entities.forEach(userTaskInstanceEntity.getReassignmentTimers()::add);
    }

    private TaskReassignmentTimerEntity buildEntity(UserTaskInstanceEntity userTaskInstanceEntity, String jobId, Reassignment reassignment, TaskReassignmentType type) {
        TaskReassignmentTimerEntity entity = new TaskReassignmentTimerEntity();
        entity.setJobId(jobId);
        entity.setTaskInstance(userTaskInstanceEntity);
        entity.setJavaType(Reassignment.class.getName());
        entity.setValue(JSONUtils.valueToString(reassignment).getBytes(StandardCharsets.UTF_8));
        entity.setType(type);
        return entity;
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Map<String, Reassignment> notStarted = new HashMap<>();
        Map<String, Reassignment> notCompleted = new HashMap<>();
        for (TaskReassignmentTimerEntity entity : userTaskInstanceEntity.getReassignmentTimers()) {
            Reassignment notification = (Reassignment) JSONUtils.stringTreeToValue(new String(entity.getValue()), Reassignment.class.getName());
            switch (entity.getType()) {
                case NotCompleted:
                    notCompleted.put(entity.getJobId(), notification);
                    break;
                case NotStarted:
                    notStarted.put(entity.getJobId(), notification);
                    break;
            }
        }

        ((DefaultUserTaskInstance) userTaskInstance).setNotStartedReassignmentsTimers(notStarted);
        ((DefaultUserTaskInstance) userTaskInstance).setNotCompletedReassignmentsTimers(notCompleted);
    }

}
