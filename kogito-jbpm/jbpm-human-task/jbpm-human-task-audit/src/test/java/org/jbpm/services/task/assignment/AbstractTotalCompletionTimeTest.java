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
package org.jbpm.services.task.assignment;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.utils.TaskFluent;
import org.jbpm.test.util.PoolingDataSource;
import org.kie.api.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTotalCompletionTimeTest extends AbstractAssignmentTest {

    protected PoolingDataSource pds;
    protected EntityManagerFactory emf;
    protected Long taskIds[];
    protected static final String DARTH_VADER = "Darth Vader";
    protected static final String BOBBA_FET = "Bobba Fet";
    protected static final String LUKE_CAGE = "Luke Cage";
    protected static final String TONY_STARK = "Tony Stark";
    protected static final String ADMIN = "Administrator";
    protected static final String DEPLOYMENT_ID = "org.jbpm:jbpm-human-task:7.1.0";
    protected static final String PROCESS_ID = "testing tasks";
    protected static final Map<String,Object> data = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(AbstractTotalCompletionTimeTest.class);

    protected long createTaskWithoutAssert(TaskFluent tf) {
        Task task = tf.getTask();
        taskService.addTask(task, data);
        return task.getId();
    }

    protected long createAndAssertTask(TaskFluent tf, String expectedOwner) {
        Task task = tf.getTask();
        taskService.addTask(task, data);
        long taskId = task.getId();
        assertEquals("Owner mismatch",expectedOwner,taskService.getTaskById(taskId).getTaskData().getActualOwner().getId());
        return taskId;
    }

    protected void completeTask(long taskId, long delay) {
        Task task = taskService.getTaskById(taskId);
        String owner = task.getTaskData().getActualOwner().getId();
        logger.debug("Starting task {} with user {}",taskId,owner);
        taskService.start(taskId, owner);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // swallowing this exception
            }
        }
        taskService.complete(taskId, owner, data);
    }
}
