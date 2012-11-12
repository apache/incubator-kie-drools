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
package org.jbpm.task.event;

import static org.jbpm.task.service.persistence.TaskPersistenceManager.addParametersToMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskEventType;
import org.jbpm.task.service.persistence.TaskPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskEventsAdminImpl implements TaskEventsAdmin {

    protected TaskPersistenceManager tpm;
    private static final Logger logger = LoggerFactory.getLogger(TaskEventsAdminImpl.class);

    public TaskEventsAdminImpl(TaskPersistenceManager tpm) {
        this.tpm = tpm;
    }

    public void storeEvent(TaskEvent event) {
        boolean txOwner = tpm.beginTransaction();
        tpm.saveEntity(event);
        tpm.endTransaction(txOwner);
    }

    public List<TaskEvent> getEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsByTaskId", params);
    }

    public List<TaskEvent> getEventsByTypeByTaskId(Long taskId, TaskEventType type) {
        Map<String, Object> params = addParametersToMap("taskId", taskId, "type", type.getValue());
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsByTypeByTaskId", params);
    }

}
