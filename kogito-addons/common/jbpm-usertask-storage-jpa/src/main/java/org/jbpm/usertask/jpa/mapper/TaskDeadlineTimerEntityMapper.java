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
import org.jbpm.usertask.jpa.model.TaskDeadlineTimerEntity;
import org.jbpm.usertask.jpa.model.TaskDeadlineType;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskDeadlineTimerRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Notification;

public class TaskDeadlineTimerEntityMapper implements EntityMapper {

    private final TaskDeadlineTimerRepository repository;

    public TaskDeadlineTimerEntityMapper(TaskDeadlineTimerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance instance, UserTaskInstanceEntity userTaskInstanceEntity) {
        if (userTaskInstanceEntity.getDeadlineTimers() == null) {
            userTaskInstanceEntity.setDeadlineTimers(new ArrayList<>());
        }
        DefaultUserTaskInstance userTaskInstance = (DefaultUserTaskInstance) instance;
        List<String> notStartedNotifications = new ArrayList<>();
        List<String> notCompletedNotifications = new ArrayList<>();

        List<TaskDeadlineTimerEntity> entities = new ArrayList<>();
        Iterator<TaskDeadlineTimerEntity> iterator = userTaskInstanceEntity.getDeadlineTimers().iterator();
        while (iterator.hasNext()) {
            TaskDeadlineTimerEntity deadlineEntity = iterator.next();
            switch (deadlineEntity.getType()) {
                case NotCompleted:
                    if (!userTaskInstance.getNotCompletedDeadlinesTimers().keySet().contains(deadlineEntity.getJobId())) {
                        entities.add(deadlineEntity);
                    } else {
                        notCompletedNotifications.add(deadlineEntity.getJobId());
                    }
                    break;
                case NotStarted:
                    if (!userTaskInstance.getNotStartedDeadlinesTimers().keySet().contains(deadlineEntity.getJobId())) {
                        entities.add(deadlineEntity);
                    } else {
                        notStartedNotifications.add(deadlineEntity.getJobId());
                    }
                    break;
            }
        }

        entities.forEach(e -> {
            repository.remove(e);
            userTaskInstanceEntity.getDeadlineTimers().remove(e);
        });
        entities.clear();

        for (Map.Entry<String, Notification> timer : userTaskInstance.getNotStartedDeadlinesTimers().entrySet()) {
            if (!notStartedNotifications.contains(timer.getKey())) {
                TaskDeadlineTimerEntity entity = buildEntity(userTaskInstanceEntity, timer.getKey(), timer.getValue(), TaskDeadlineType.NotStarted);
                entities.add(entity);
            }
        }

        for (Map.Entry<String, Notification> timer : userTaskInstance.getNotCompletedDeadlinesTimers().entrySet()) {
            if (!notCompletedNotifications.contains(timer.getKey())) {
                TaskDeadlineTimerEntity entity = buildEntity(userTaskInstanceEntity, timer.getKey(), timer.getValue(), TaskDeadlineType.NotCompleted);
                entities.add(entity);
            }
        }
        entities.forEach(userTaskInstanceEntity.getDeadlineTimers()::add);
    }

    private TaskDeadlineTimerEntity buildEntity(UserTaskInstanceEntity userTaskInstanceEntity, String jobId, Notification deadline, TaskDeadlineType type) {
        TaskDeadlineTimerEntity entity = new TaskDeadlineTimerEntity();
        entity.setTaskInstance(userTaskInstanceEntity);
        entity.setJobId(jobId);
        entity.setJavaType(Notification.class.getName());
        entity.setValue(JSONUtils.valueToString(deadline).getBytes(StandardCharsets.UTF_8));
        entity.setType(type);
        return entity;
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Map<String, Notification> notStarted = new HashMap<>();
        Map<String, Notification> notCompleted = new HashMap<>();
        for (TaskDeadlineTimerEntity entity : userTaskInstanceEntity.getDeadlineTimers()) {
            Notification notification = (Notification) JSONUtils.stringTreeToValue(new String(entity.getValue()), Notification.class.getName());
            switch (entity.getType()) {
                case NotCompleted:
                    notCompleted.put(entity.getJobId(), notification);
                    break;
                case NotStarted:
                    notStarted.put(entity.getJobId(), notification);
                    break;
            }
        }

        ((DefaultUserTaskInstance) userTaskInstance).setNotStartedDeadlinesTimers(notStarted);
        ((DefaultUserTaskInstance) userTaskInstance).setNotCompletedDeadlinesTimers(notCompleted);
    }

    private Notification readNotification(byte[] value) {
        return (Notification) JSONUtils.stringTreeToValue(new String(value), Notification.class.getName());
    }
}
