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
package org.jbpm.services.task.audit.service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.junit.After;
import org.junit.Before;
import org.kie.internal.task.api.InternalTaskService;

import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.utils.TaskFluent;
import org.jbpm.test.util.PoolingDataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;

public class LocalWithCustomCallbackTaskAuditTest extends HumanTaskServicesBaseTest {

    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private NullGroupsUserGroupCallback callback;

    @Inject
    protected TaskAuditService taskAuditService;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        callback = new NullGroupsUserGroupCallback();
        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory(emf)
                .listener(new JPATaskLifeCycleEventListener(true))
                .listener(new BAMTaskEventListener(true))
                .userGroupCallback(callback)
                .getTaskService();

        this.taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService)
                .getTaskAuditService();
    }

    @After
    public void clean() {
        if (emf != null) {
            emf.close();
        }
        if (pds != null) {
            pds.close();
        }
    }

    private class NullGroupsUserGroupCallback implements UserGroupCallback {

        @Override
        public boolean existsUser(String userId) {

            return true;
        }

        @Override
        public boolean existsGroup(String groupId) {

            return true;
        }

        @Override
        public List<String> getGroupsForUser(String userId) {

            return null;
        }

    }

    @Test
    public void testGroupTasks() {
        Task task = new TaskFluent().setName("This is my task name")
                .addPotentialUser("salaboy")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> allGroupTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupTasks.size());
        assertTrue(allGroupTasks.get(0).getStatusId().equals("Ready"));

        List<AuditTask> allGroupAuditTasksByUser = taskAuditService.getAllGroupAuditTasksByUser("salaboy",
                new QueryFilter(0, 0));
        assertEquals(1, allGroupAuditTasksByUser.size());
        assertTrue(allGroupAuditTasksByUser.get(0).getStatus().equals("Ready"));
    }
}
