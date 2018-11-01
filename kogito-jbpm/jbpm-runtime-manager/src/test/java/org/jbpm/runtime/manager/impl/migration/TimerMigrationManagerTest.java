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

package org.jbpm.runtime.manager.impl.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

@RunWith(Parameterized.class)
public class TimerMigrationManagerTest extends AbstractBaseTest {
    
    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {"singleton"}, 
                 {"processinstance"}
           });
    }
    
    private String strategy;
       
    public TimerMigrationManagerTest(String strategy) {
        this.strategy = strategy;
    }

    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager managerV1;
    private RuntimeManager managerV2;
    
    // general info
    private static final String DEPLOYMENT_ID_V1 = "managerV1";
    private static final String DEPLOYMENT_ID_V2 = "managerV2";
    
    private static final String TIMER_ID_V1 = "Timer-V1";
    private static final String TIMER_ID_V2 = "Timer-V2";
    
    private static final String BOUNDARY_TIMER_ID_V1 = "TimerBoundaryEventV1";
    private static final String BOUNDARY_TIMER_ID_V2 = "TimerBoundaryEventV2";
    
    private static final String EVENT_SUBPROCESS_TIMER_ID_V1 = "BPMN2-EventSubprocessTimerV1";
    private static final String EVENT_SUBPROCESS_TIMER_ID_V2 = "BPMN2-EventSubprocessTimerV2";
    
    private static final String CYCLE_TIMER_ID_V1 = "CycleTimer-V1";
    private static final String CYCLE_TIMER_ID_V2 = "CycleTimer-V2";
    
    private static final String LOOP_TIMER_ID_V1 = "ProcessClaim.CheckDisruption-v1";
    private static final String LOOP_TIMER_ID_V2 = "ProcessClaim.CheckDisruption-v2";
    
    private JPAAuditLogService auditService;
    
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        auditService = new JPAAuditLogService(emf);
    }
    
    @After
    public void teardown() {
        auditService.dispose();
        
        if (managerV1 != null) {
            managerV1.close();
        }
        if (managerV2 != null) {
            managerV2.close();
        }
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }
    
   
    
    @Test(timeout=10000)
    public void testMigrateTimerProcessInstance() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("EventV2", 1);
        createRuntimeManagers("migration/v1/BPMN2-Timer-v1.bpmn2", "migration/v2/BPMN2-Timer-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(TIMER_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());

    }
    
    @Test(timeout=10000)
    public void testMigrateBoundaryTimerProcessInstance() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("GoodbyeV2", 1);
        createRuntimeManagers("migration/v1/BPMN2-TimerBoundary-v1.bpmn2", "migration/v2/BPMN2-TimerBoundary-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(BOUNDARY_TIMER_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(BOUNDARY_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, BOUNDARY_TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(BOUNDARY_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(BOUNDARY_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());

    }
    
    @Test(timeout=10000)
    public void testMigrateEventSubprocessTimerProcessInstance() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("Script Task 1 V2", 1);
        createRuntimeManagers("migration/v1/BPMN2-EventSubprocessTimer-v1.bpmn2", "migration/v2/BPMN2-EventSubprocessTimer-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(EVENT_SUBPROCESS_TIMER_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(EVENT_SUBPROCESS_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, EVENT_SUBPROCESS_TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(EVENT_SUBPROCESS_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(EVENT_SUBPROCESS_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ABORTED, log.getStatus().intValue());

    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test(timeout=10000)
    public void testMigrateTimerProcessInstanceRollback() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("Event", 1);
        createRuntimeManagers("migration/v1/BPMN2-Timer-v1.bpmn2", "migration/v2/BPMN2-Timer-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(TIMER_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = null;              
        try {
            // explicitly without generic to cause error (class cast) in migration process to test rollback
            Map erronousMapping = Collections.singletonMap("_3", 3);            
            migrationManager.migrate(erronousMapping);
        } catch (MigrationException e) {
           report = e.getReport(); 
        }        
        assertNotNull(report);
        assertFalse(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());

    }
    
    @Test(timeout=20000)
    public void testMigrateTimerCycleProcessInstance() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("print smt", 2);
        createRuntimeManagers("migration/v1/BPMN2-TimerCycle-v1.bpmn2", "migration/v2/BPMN2-TimerCycle-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        Map<String, Object> params = Collections.singletonMap("startTime", Instant.now().toString());
        ProcessInstance pi1 = ksession.startProcess(CYCLE_TIMER_ID_V1, params);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        // wait for first timer expiration before migration
        countdownListener.waitTillCompleted();
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, CYCLE_TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires
        countdownListener.reset(1);
        countdownListener.waitTillCompleted();
        
        runtime = managerV2.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ksession.signalEvent("endMe", null, pi1.getId());
                
        managerV2.disposeRuntimeEngine(runtime);
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());

    }

    @Test(timeout=20000)
    public void testMigrateTimerCycleProcessInstanceBeforeFirstTrigger() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("print smt", 3);
        createRuntimeManagers("migration/v1/BPMN2-TimerCycle-v1.bpmn2", "migration/v2/BPMN2-TimerCycle-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);

        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        // Delay first firing to have enough time for migration
        Map<String, Object> params = Collections.singletonMap("startTime", Instant.now().plusSeconds(3).toString());
        ProcessInstance pi1 = ksession.startProcess(CYCLE_TIMER_ID_V1, params);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());

        managerV1.disposeRuntimeEngine(runtime);

        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, CYCLE_TIMER_ID_V2);

        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();

        assertNotNull(report);
        assertTrue(report.isSuccessful());

        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());

        // wait till timer fires 3 times, reset the counter to be sure all timer triggers fired after migration
        countdownListener.reset(3);
        countdownListener.waitTillCompleted();

        runtime = managerV2.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.signalEvent("endMe", null, pi1.getId());

        managerV2.disposeRuntimeEngine(runtime);

        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(CYCLE_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());

    }
    
    @Test(timeout=40000)
    public void testMigrateTimerWithLoopProcessInstance() throws Exception {
        NodeLeftCountDownProcessEventListener countdownListener = new NodeLeftCountDownProcessEventListener("3s timer", 1);
        createRuntimeManagers("migration/v1/CheckDisruption-v1.bpmn2", "migration/v2/CheckDisruption-v2.bpmn2", countdownListener);
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(LOOP_TIMER_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(LOOP_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
                
        managerV1.disposeRuntimeEngine(runtime);
        
        // wait till timer fires for the first iteration
        countdownListener.waitTillCompleted();
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, LOOP_TIMER_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(LOOP_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires for next iterations already on migrated process instance
        countdownListener.reset(1);
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        
        assertNotNull(log);
        assertEquals(LOOP_TIMER_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V2, pi1.getId(), DEPLOYMENT_ID_V1, LOOP_TIMER_ID_V1);
        
        migrationManager = new MigrationManager(migrationSpec);
        report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(LOOP_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        
        // wait till timer fires for next iterations already on migrated process instance
        countdownListener.reset(1);
        countdownListener.waitTillCompleted();
        
        log = auditService.findProcessInstance(pi1.getId());
        auditService.dispose();
        assertNotNull(log);
        assertEquals(LOOP_TIMER_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        assertEquals(ProcessInstance.STATE_ACTIVE, log.getStatus().intValue());
        
        runtime = managerV1.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        
        ksession.abortProcessInstance(pi1.getId());
        
        managerV1.disposeRuntimeEngine(runtime);

    }
    
    protected void createRuntimeManagers(String processV1, String processV2, ProcessEventListener...eventListeners) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(processV1), ResourceType.BPMN2)  
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        
                        for (ProcessEventListener lister : eventListeners) {
                            listeners.add(lister);
                        }
                        
                        return listeners;
                    }
                    
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        
                        handlers.put("MyTask", new DoNothingWorkItemHandler());
                        
                        return handlers;
                    }
     
                })
                .get();
                 
        
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(processV2), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        
                        for (ProcessEventListener lister : eventListeners) {
                            listeners.add(lister);
                        }
                        
                        return listeners;
                    }

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        
                        handlers.put("MyTask", new DoNothingWorkItemHandler());
                        
                        return handlers;
                    }
     
                })
                .get();
        
        createRuntimeManager(environment, environment2);
    }
    
    private void createRuntimeManager(RuntimeEnvironment environment, RuntimeEnvironment environment2) {
              
        if ("singleton".equals(strategy)) {
            managerV1 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, DEPLOYMENT_ID_V1);
            managerV2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, DEPLOYMENT_ID_V2); 
        } else if ("processinstance".equals(strategy)) {
            managerV1 = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, DEPLOYMENT_ID_V1);
            managerV2 = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment2, DEPLOYMENT_ID_V2); 
        } 
        assertNotNull(managerV1);
        assertNotNull(managerV2);
    }
}
