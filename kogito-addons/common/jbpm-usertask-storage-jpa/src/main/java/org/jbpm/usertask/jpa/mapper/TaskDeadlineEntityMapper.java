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
import java.util.Iterator;
import java.util.List;

import org.jbpm.usertask.jpa.mapper.json.utils.JSONUtils;
import org.jbpm.usertask.jpa.model.TaskDeadlineEntity;
import org.jbpm.usertask.jpa.model.TaskDeadlineType;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskDeadlineRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.DeadlineInfo;
import org.kie.kogito.usertask.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;

public class TaskDeadlineEntityMapper implements EntityMapper {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDeadlineEntityMapper.class);

    private final TaskDeadlineRepository repository;

    public TaskDeadlineEntityMapper(TaskDeadlineRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        if (userTaskInstanceEntity.getDeadlines() == null) {
            userTaskInstanceEntity.setDeadlines(new ArrayList<>());
        }

        List<TaskDeadlineEntity> entities = new ArrayList<>();
        List<DeadlineInfo<Notification>> notStartedNotifications = new ArrayList<>();
        List<DeadlineInfo<Notification>> notCompletedNotifications = new ArrayList<>();
        Iterator<TaskDeadlineEntity> iterator = userTaskInstanceEntity.getDeadlines().iterator();
        while (iterator.hasNext()) {
            TaskDeadlineEntity deadlineEntity = iterator.next();
            DeadlineInfo<Notification> deadline = readNotification(deadlineEntity.getValue());
            switch (deadlineEntity.getType()) {
                case NotCompleted:
                    if (!userTaskInstance.getNotCompletedDeadlines().contains(deadline)) {
                        entities.add(deadlineEntity);
                    } else {
                        notCompletedNotifications.add(deadline);
                    }
                    break;
                case NotStarted:
                    if (!userTaskInstance.getNotStartedDeadlines().contains(deadline)) {
                        entities.add(deadlineEntity);
                    } else {
                        notStartedNotifications.add(deadline);
                    }
                    break;
            }
        }

        entities.forEach(e -> {
            repository.remove(e);
            userTaskInstanceEntity.getDeadlines().remove(e);
        });
        entities.clear();

        for (DeadlineInfo<Notification> deadline : userTaskInstance.getNotStartedDeadlines()) {
            if (!notStartedNotifications.contains(deadline)) {
                entities.add(buildEntity(userTaskInstanceEntity, deadline, TaskDeadlineType.NotStarted));
            }
        }
        for (DeadlineInfo<Notification> deadline : userTaskInstance.getNotCompletedDeadlines()) {
            if (!notCompletedNotifications.contains(deadline)) {
                entities.add(buildEntity(userTaskInstanceEntity, deadline, TaskDeadlineType.NotCompleted));
            }
        }
        entities.forEach(e -> {
            userTaskInstanceEntity.getDeadlines().add(e);
        });
    }

    private TaskDeadlineEntity buildEntity(UserTaskInstanceEntity userTaskInstanceEntity, DeadlineInfo<Notification> deadline, TaskDeadlineType type) {
        TaskDeadlineEntity entity = new TaskDeadlineEntity();
        entity.setTaskInstance(userTaskInstanceEntity);
        entity.setJavaType(DeadlineInfo.class.getName());
        entity.setValue(JSONUtils.valueToString(deadline).getBytes(StandardCharsets.UTF_8));
        entity.setType(type);
        return entity;
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        List<DeadlineInfo<Notification>> notStarted = new ArrayList<>();
        List<DeadlineInfo<Notification>> notCompleted = new ArrayList<>();

        for (TaskDeadlineEntity entity : userTaskInstanceEntity.getDeadlines()) {
            DeadlineInfo<Notification> deadline = readNotification(entity.getValue());
            switch (entity.getType()) {
                case NotCompleted:
                    notCompleted.add(deadline);
                    break;
                case NotStarted:
                    notStarted.add(deadline);
                    break;
            }
        }

        ((DefaultUserTaskInstance) userTaskInstance).setNotStartedDeadlines(notStarted);
        ((DefaultUserTaskInstance) userTaskInstance).setNotCompletedDeadlines(notCompleted);
    }

    private DeadlineInfo<Notification> readNotification(byte[] value) {
        JavaType javaType = JSONUtils.buildJavaType(DeadlineInfo.class, Notification.class);
        return (DeadlineInfo<Notification>) JSONUtils.stringTreeToValue(new String(value), javaType);
    }
}
