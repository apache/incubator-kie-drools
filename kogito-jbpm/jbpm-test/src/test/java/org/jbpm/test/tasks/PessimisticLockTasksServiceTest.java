/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.test.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PessimisticLockException;
import javax.transaction.UserTransaction;

import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PessimisticLockTasksServiceTest extends JbpmJUnitBaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(PessimisticLockTasksServiceTest.class);

    private EntityManagerFactory emfTasks;
    protected Map<String, User> users;
    protected Map<String, Group> groups;

    protected Properties conf;

    protected ExternalTaskEventListener externalTaskEventListener;


    public PessimisticLockTasksServiceTest() {
        super(true, true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPessimisticLockingOnTask() throws Exception {
        final List<Exception> exceptions = new ArrayList<Exception>();
        addEnvironmentEntry(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);

        createRuntimeManager("Evaluation2.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        final KieSession ksession = runtimeEngine.getKieSession();
        final TaskService taskService = runtimeEngine.getTaskService();


        // setup another instance of task service to allow not synchronized access to cause pessimistic lock exception
        RuntimeEnvironment runtimeEnv = ((InternalRuntimeManager) manager).getEnvironment();
        HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator()
                .environment(runtimeEnv.getEnvironment())
                .entityManagerFactory((EntityManagerFactory) runtimeEnv.getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY))
                .userGroupCallback(runtimeEnv.getUserGroupCallback());
        // register task listeners if any
        RegisterableItemsFactory itemsFactory = runtimeEnv.getRegisterableItemsFactory();
        for (TaskLifeCycleEventListener taskListener : itemsFactory.getTaskListeners()) {
            configurator.listener(taskListener);
        }

        final TaskService internalTaskService = configurator.getTaskService();

        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee", "salaboy");
        ProcessInstance process = ksession.startProcess("com.sample.evaluation", parameters);


        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets salaboy's tasks
        List<TaskSummary> salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Assert.assertEquals(1, salaboysTasks.size());

        final long taskId = salaboysTasks.get(0).getId();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                    ut.begin();
                    logger.info("Attempting to lock task instance for 3 secs ");
                    taskService.start(taskId, "salaboy");

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("Unlocked task instance after 3 secs");
                    ut.rollback();
                } catch (Exception e) {
                    logger.error("Error on thread ", e);
                }
            }

        };

        t1.start();
        Thread.sleep(1000);

        Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                    ut.begin();
                    logger.info("Trying to start locked task instance");
                    try {

                        internalTaskService.start(taskId, "salaboy");

                    } catch (Exception e) {
                        logger.info("Abort failed with error {}", e.getMessage());
                        exceptions.add(e);

                    } finally {
                        ut.rollback();
                    }
                } catch (Exception e) {
                    logger.error("Error on thread ", e);
                }
            }
        };
        t2.start();

        Thread.sleep(3000);

        assertEquals(1, exceptions.size());
        assertEquals(PessimisticLockException.class.getName(), exceptions.get(0).getClass().getName());


        taskService.start(salaboysTasks.get(0).getId(), "salaboy");

        // complete task within user transaction to make sure no deadlock happens as both task service and ksession are under tx lock
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        taskService.complete(salaboysTasks.get(0).getId(), "salaboy", null);
        ut.commit();


        List<TaskSummary> pmsTasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(1, pmsTasks.size());


        List<TaskSummary> hrsTasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(1, hrsTasks.size());

        ksession.abortProcessInstance(process.getId());
        assertProcessInstanceAborted(process.getId());
    }

}
