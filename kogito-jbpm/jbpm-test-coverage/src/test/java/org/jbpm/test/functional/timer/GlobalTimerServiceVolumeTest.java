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

package org.jbpm.test.functional.timer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class GlobalTimerServiceVolumeTest extends TimerBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GlobalTimerServiceVolumeTest.class);
    private UserGroupCallback userGroupCallback;

    private GlobalSchedulerService globalScheduler;

    private RuntimeManager manager;

    private EntityManagerFactory emf;

    private int numberOfProcesses = 10;
    private NodeLeftCountDownProcessEventListener countDownListener;

    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 {"request"},
                 {"processinstance"},
                 {"case"}
           });
    }

    private String strategy;

    public GlobalTimerServiceVolumeTest(String strategy) {
        this.strategy = strategy;
    }

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup() {

        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        System.setProperty("org.quartz.properties", "quartz-db.properties");
        testCreateQuartzSchema();
        globalScheduler = new QuartzSchedulerService();

        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");

        countDownListener = new NodeLeftCountDownProcessEventListener("timer", numberOfProcesses);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    timerExporations.add(event.getProcessInstance().getId());
                }
            }

        };

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycleWithHT3.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener))
                .get();

        if ("processinstance".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");
        } else if ("request".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");
        } else if ("case".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment, "first");
        } else {
            throw new RuntimeException("Unknow type of runtime strategy");
        }

    }

    @After
    public void tearDown() {
        globalScheduler.shutdown();
        if (manager != null) {
            manager.close();
        }
        emf.close();
    }

    @Test(timeout=30000)
    public void testRuntimeManagerStrategyWithTimerService() throws Exception {

        // prepare task service with users and groups
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = engine.getTaskService();


        Group grouphr = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) grouphr).setId("HR");
        Group groupadmins = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) groupadmins).setId("Administrators");


        User mary = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) mary).setId("mary");
        User john = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) john).setId("john");
        User admin = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) admin).setId("Administrator");

        ((InternalTaskService) taskService).addGroup(grouphr);
        ((InternalTaskService) taskService).addGroup(groupadmins);
        ((InternalTaskService) taskService).addUser(mary);
        ((InternalTaskService) taskService).addUser(john);
        ((InternalTaskService) taskService).addUser(admin);


        manager.disposeRuntimeEngine(engine);

        int counter = numberOfProcesses;
        // start processes until oom
        while (counter > 0) {
            new GlobalTimerServiceVolumeTest.StartProcessPerProcessInstanceRunnable(manager).run();
            counter--;
        }

        Collection<TimerJobInstance> timers = null;
        Map<Long, List<GlobalJobHandle>> jobs = null;
        TimerService timerService = TimerServiceRegistry.getInstance().get(manager.getIdentifier() + TimerServiceRegistry.TIMER_SERVICE_SUFFIX);
        if (timerService != null) {
            if (timerService instanceof GlobalTimerService) {
                jobs = ((GlobalTimerService) timerService).getTimerJobsPerSession();
                
                timers = ((GlobalTimerService) timerService).getTimerJobFactoryManager().getTimerJobInstances();
            }
        }

        assertNotNull("Jobs should not be null as number of timers have been created", jobs);
        assertEquals("There should be no jobs in the global timer service", 0, jobs.size());
        
        assertNotNull("Timer instances should not be null as number of timers have been created", timers);
        assertEquals("There should be no timer instances in the global timer service manager", 0, timers.size());

        RuntimeEngine empty = manager.getRuntimeEngine(EmptyContext.get());
        AuditService logService = empty.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("IntermediateCatchEvent");
        assertEquals("Active process instances should be " + numberOfProcesses, numberOfProcesses, logs.size());

        countDownListener.waitTillCompleted();

        List<TaskSummary> tasks = empty.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals("Number of John's tasks should be " + numberOfProcesses, numberOfProcesses, tasks.size());

        for (TaskSummary task : tasks) {

            RuntimeEngine piEngine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));

            piEngine.getTaskService().start(task.getId(), "john");
            piEngine.getTaskService().complete(task.getId(), "john", null);

            manager.disposeRuntimeEngine(piEngine);
        }

        logs = logService.findActiveProcessInstances("IntermediateCatchEvent");
        assertEquals("Active process instances should be 0", 0, logs.size());
        logService.dispose();
        manager.disposeRuntimeEngine(empty);

    }


    private void testStartProcess(RuntimeEngine runtime) throws Exception {

        synchronized ((SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) runtime.getKieSession()).getRunner()) {
            UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            try {
                ut.begin();
                logger.debug("Starting process on ksession {}", runtime.getKieSession().getIdentifier());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("x", "5s");
                ProcessInstance processInstance = runtime.getKieSession().startProcess("IntermediateCatchEvent", params);
                logger.debug("Started process instance {} on ksession {}", processInstance.getId(), runtime.getKieSession().getIdentifier());
                ut.commit();
            } catch (Exception ex) {
                ut.rollback();
                throw ex;
            }
        }


    }


    public class StartProcessPerProcessInstanceRunnable implements Runnable {
        private RuntimeManager manager;

        public StartProcessPerProcessInstanceRunnable(RuntimeManager manager) {
            this.manager = manager;
        }

        public void run() {
            try {
                RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                testStartProcess(runtime);
                manager.disposeRuntimeEngine(runtime);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
