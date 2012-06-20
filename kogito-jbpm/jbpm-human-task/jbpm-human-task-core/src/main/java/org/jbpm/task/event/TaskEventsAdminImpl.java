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

import java.util.HashMap;
import java.util.List;
import javax.persistence.Query;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.IncorrectParametersException;
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

    public List<TaskEvent> getEventsByTypeByTaskId(Long taskId, String type) {
        boolean txOwner = tpm.beginTransaction();

        Query query = tpm.createNewNativeQuery("select * from TaskEvent te where te.taskId = :taskId and te.DTYPE = :type");
        query.setParameter("taskId", taskId);
        query.setParameter("type", type);

        List<TaskEvent> list = query.getResultList();
        tpm.endTransaction(txOwner);

        return (List<TaskEvent>) list;
    }

    public static HashMap<String, Object> addParametersToMap(Object... parameterValues) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        if (parameterValues.length % 2 != 0) {
            throw new IncorrectParametersException("Expected an even number of parameters, not " + parameterValues.length);
        }

        for (int i = 0; i < parameterValues.length; ++i) {
            String parameterName = null;
            if (parameterValues[i] instanceof String) {
                parameterName = (String) parameterValues[i];
            } else {
                throw new IncorrectParametersException("Expected a String as the parameter name, not a " + parameterValues[i].getClass().getSimpleName());
            }
            ++i;
            parameters.put(parameterName, parameterValues[i]);
        }

        return parameters;
    }

    public List<TaskEvent> getTaskForwardedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsForwardedByTaskId", params);
    }

    public List<TaskEvent> getTaskClaimedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsClaimedByTaskId", params);
    }

    public List<TaskEvent> getTaskCompletedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsCompletedByTaskId", params);
    }

    public List<TaskEvent> getTaskFailedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsFailedByTaskId", params);
    }

    public List<TaskEvent> getTaskSkippedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsSkippedByTaskId", params);
    }

    public List<TaskEvent> getTaskStartedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsStartedByTaskId", params);
    }

    public List<TaskEvent> getTaskStoppedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsStoppedByTaskId", params);
    }

    public List<TaskEvent> getTaskCreatedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsCreatedByTaskId", params);
    }

    public List<TaskEvent> getTaskReleasedEventsByTaskId(Long taskId) {
        HashMap<String, Object> params = addParametersToMap("id", taskId);
        return (List<TaskEvent>) tpm.queryWithParametersInTransaction("TaskEventsReleasedByTaskId", params);
    }
}
