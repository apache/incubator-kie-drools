package org.jbpm.test.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.After;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.TaskSummary;


public abstract class GlobalTimerServiceBaseTest extends TimerBaseTest{
    
    protected GlobalSchedulerService globalScheduler;
    protected RuntimeManager manager;
    protected RuntimeEnvironment environment;
   
    protected abstract RuntimeManager getManager(RuntimeEnvironment environment);
    
    @After
    public void cleanup() {
        manager.close();
        EntityManagerFactory emf = ((SimpleRuntimeEnvironment) environment).getEmf();
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testInterediateTiemrWithGlobalTestService() throws Exception {
        
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
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();


        manager = getManager(environment);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for first timer to trigger
        Thread.sleep(1500);
        // dispose session to force session to be reloaded on timer expiration
        manager.disposeRuntimeEngine(runtime);
        Thread.sleep(2000);
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();
        ksession.abortProcessInstance(processInstance.getId());
        processInstance = ksession.getProcessInstance(processInstance.getId());        
        assertNull(processInstance);
        // let's wait to ensure no more timers are expired and triggered
        Thread.sleep(3000);
   
        assertEquals(3, timerExporations.size());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testTimerStart() throws Exception {
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                timerExporations.add(event.getProcessInstance().getId());
            }

 
            
        };
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-TimerStart2.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        
        manager = getManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        
        assertEquals(0, timerExporations.size());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
        }
        Thread.sleep(200);
        manager.disposeRuntimeEngine(runtime);
        assertEquals(5, timerExporations.size());

    }

    @Test
    public void testTimerRule() throws Exception {
        // prepare listener to assert results
        final List<String> timerExporations = new ArrayList<String>();
        AgendaEventListener listener = new DefaultAgendaEventListener(){

            @Override
            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                timerExporations.add(event.getMatch().getRule().getId());
            }

        };
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("timer-rules.drl"), ResourceType.DRL)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        
        manager = getManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        
        assertEquals(0, timerExporations.size());
        for (int i = 0; i < 5; i++) {
            runtime.getKieSession().fireAllRules();
            Thread.sleep(1000);
        }
        
        manager.disposeRuntimeEngine(runtime);
        assertEquals(5, timerExporations.size());
    }
    
    @Test
    public void testInterediateTiemrWithHTAfterWithGlobalTestService() throws Exception {
        
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
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycleWithHT.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .userGroupCallback(userGroupCallback)
                .get();
       
        manager = getManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "1s###1s");
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        System.out.println("Disposed after start");
        // dispose session to force session to be reloaded on timer expiration
        manager.disposeRuntimeEngine(runtime);
        // now wait for 1 second for first timer to trigger
        Thread.sleep(1500);
        
        Thread.sleep(2000);
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();
        
        // get tasks
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwnerByStatus("john", statuses, "en-UK");
        assertNotNull(tasks);
        assertEquals(3, tasks.size());
        
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        
        ksession.abortProcessInstance(processInstance.getId());
        processInstance = ksession.getProcessInstance(processInstance.getId());        
        assertNull(processInstance);
        // let's wait to ensure no more timers are expired and triggered
        Thread.sleep(3000);
   
        
        manager.disposeRuntimeEngine(runtime);

        assertEquals(3, timerExporations.size());
    }
    
    @Test
    public void testInterediateTiemrWithHTBeforeWithGlobalTestService() throws Exception {
        
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
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycleWithHT2.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .userGroupCallback(userGroupCallback)
                .get();
                
        manager = getManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "1s###1s");
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        // get tasks
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwnerByStatus("john", statuses, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        // dispose session to force session to be reloaded on timer expiration
        manager.disposeRuntimeEngine(runtime);
        // now wait for 1 second for first timer to trigger
        Thread.sleep(1500);
        
        Thread.sleep(2000);
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();

        
        ksession.abortProcessInstance(processInstance.getId());
        processInstance = ksession.getProcessInstance(processInstance.getId());        
        assertNull(processInstance);
        // let's wait to ensure no more timers are expired and triggered
        Thread.sleep(3000);
   
   
        manager.disposeRuntimeEngine(runtime);

        assertEquals(3, timerExporations.size());
    }
    
    @Test
    public void testInterediateTiemrWithGlobalTestServiceRollback() throws Exception {
        
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .get();
        manager = getManager(environment);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        int ksessionId = ksession.getId();
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        // roll back
        ut.rollback();
        manager.disposeRuntimeEngine(runtime);
        try {
            // two types of checks as different managers will treat it differently
            // per process instance will fail on getting runtime
            runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
            // where singleton and per request will return runtime but there should not be process instance
            processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
            assertNull(processInstance);
        } catch (SessionNotFoundException e) {
            
        }

        TimerService timerService = TimerServiceRegistry.getInstance().get(manager.getIdentifier()+"-timerServiceId");
        Collection<TimerJobInstance> timerInstances = timerService.getTimerJobInstances(ksessionId);
        assertNotNull(timerInstances);
        assertEquals(0, timerInstances.size());
        
        if (runtime != null) {
            manager.disposeRuntimeEngine(runtime);
        }
    }
    
    @Test
    public void testInterediateTiemrWithHTBeforeWithGlobalTestServiceRollback() throws Exception {
        
        // prepare listener to assert results
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycleWithHT2.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = getManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        
        int ksessionId = ksession.getId();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "1s###1s");
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        // get tasks
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwnerByStatus("john", statuses, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        runtime.getTaskService().start(task.getId(), "john");
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        runtime.getTaskService().complete(task.getId(), "john", null);// roll back
        ut.rollback();
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        assertTrue(activeNodes.iterator().next() instanceof HumanTaskNodeInstance);

        TimerService timerService = TimerServiceRegistry.getInstance().get(manager.getIdentifier()+"-timerServiceId");
        Collection<TimerJobInstance> timerInstances = timerService.getTimerJobInstances(ksessionId);
        assertNotNull(timerInstances);
        assertEquals(0, timerInstances.size());
        
        // clean up
        ksession.abortProcessInstance(processInstance.getId());
        
        manager.disposeRuntimeEngine(runtime);

    }
    
    @Test
    public void testInterediateBoundaryTimerWithGlobalTestServiceRollback() throws Exception {
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        environment = RuntimeEnvironmentBuilder.getDefault()
                .addAsset(ResourceFactory.newClassPathResource("HumanTaskWithBoundaryTimer.bpmn"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .userGroupCallback(userGroupCallback)
                .get();

        manager = getManager(environment);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        int ksessionId = ksession.getId();
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "john");
        ProcessInstance processInstance = ksession.startProcess("PROCESS_1", params);
        // roll back
        ut.rollback();
        manager.disposeRuntimeEngine(runtime);
        try {
            // two types of checks as different managers will treat it differently
            // per process instance will fail on getting runtime
            runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
            // where singleton and per request will return runtime but there should not be process instance
            processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
            assertNull(processInstance);
        } catch (SessionNotFoundException e) {
            
        }

        TimerService timerService = TimerServiceRegistry.getInstance().get(manager.getIdentifier()+"-timerServiceId");
        Collection<TimerJobInstance> timerInstances = timerService.getTimerJobInstances(ksessionId);
        assertNotNull(timerInstances);
        assertEquals(0, timerInstances.size());
        
        if (runtime != null) {
            manager.disposeRuntimeEngine(runtime);
        }
    }
    
    
    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                
                new File(tempDir, file).delete();
            }
        }
    }
}
