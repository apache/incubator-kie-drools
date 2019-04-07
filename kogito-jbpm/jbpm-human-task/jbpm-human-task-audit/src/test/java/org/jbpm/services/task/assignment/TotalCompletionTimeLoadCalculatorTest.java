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

import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.TotalCompletionTimeLoadCalculator;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.services.task.persistence.JPATaskPersistenceContext;
import org.jbpm.services.task.utils.TaskFluent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.InternalTaskService;

public class TotalCompletionTimeLoadCalculatorTest extends AbstractTotalCompletionTimeTest {

    /**
     * Creates tasks and completes them so that there is
     * BAMTaskSummary data
     */
    private void forceBAMEntries() {
        for (int count = 0; count < 4; count++) {
            TaskFluent task1 = new TaskFluent()
                    .setName("CalculatorTask1")
                    .addPotentialUser(DARTH_VADER)
                    .setAdminUser(ADMIN)
                    .setDeploymentID(DEPLOYMENT_ID)
                    .setProcessId(PROCESS_ID);
            long taskId = createTaskWithoutAssert(task1);
            int waitTime = 100;
            if (count%2 == 0) {
                waitTime = 180;
            }
            completeTask(taskId,waitTime);
        }
    }

    @Before
    public void setUp() throws Exception {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory(emf)
                .listener(new JPATaskLifeCycleEventListener(true))
                .listener(new BAMTaskEventListener(true))
                .getTaskService();
        taskIds = new Long[100]; // giving ourselves lots of room
        forceBAMEntries();
    }

    @After
    public void clean() throws Exception {
        if (emf != null) {
            emf.close();
        }
        if (pds != null) {
            pds.close();
        }
    }

    @Test
    public void testGetUserTaskLoad() {
        // Prepare and claim 6 tasks by The Emperor
        for (int x = 0; x < 6; x++) {
            TaskFluent task = new TaskFluent()
                    .setName("CalculatorTask1")
                    .addPotentialUser(DARTH_VADER)
                    .setDeploymentID(DEPLOYMENT_ID)
                    .setProcessId(PROCESS_ID)
                    .setAdminUser(ADMIN);
            createTaskWithoutAssert(task);
        }

        User userVader = new UserImpl(DARTH_VADER);

        TaskContext ctx = new TaskContext();
        EntityManager em = emf.createEntityManager();
        ctx.setPersistenceContext(new JPATaskPersistenceContext(em));

        LoadCalculator calculator = new TotalCompletionTimeLoadCalculator();

        Double userTaskLoad = calculator.getUserTaskLoad(userVader, ctx).getCalculatedLoad();

        Double userTaskLoads = calculator.getUserTaskLoads(Collections.singletonList(userVader), ctx).iterator().next().getCalculatedLoad();

        Assertions.assertThat(userTaskLoad).isGreaterThanOrEqualTo(840.0);
        Assertions.assertThat(userTaskLoads).isEqualTo(userTaskLoad);

        em.close();
    }

}
