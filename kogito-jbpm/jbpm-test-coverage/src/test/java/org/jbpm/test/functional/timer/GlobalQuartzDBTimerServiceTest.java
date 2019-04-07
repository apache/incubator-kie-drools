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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class GlobalQuartzDBTimerServiceTest extends GlobalTimerServiceBaseTest {

    private int managerType;
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }  };
        return Arrays.asList(data);
    };
    
    public GlobalQuartzDBTimerServiceTest(int managerType) {
        this.managerType = managerType;
    }
    
    @Before
    public void setUp() {
        cleanupSingletonSessionId();
        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
        System.setProperty("org.quartz.properties", "quartz-db.properties");
        testCreateQuartzSchema();
        globalScheduler = new QuartzSchedulerService();
        ((QuartzSchedulerService)globalScheduler).forceShutdown();
    }
    
    @After
    public void tearDown() {
        try {
            
            globalScheduler.shutdown();
        } catch (Exception e) {
            
        }
        cleanup();
        System.clearProperty("org.quartz.properties");
    }   
    
    @Override
    protected RuntimeManager getManager(RuntimeEnvironment environment, boolean waitOnStart) {
        RuntimeManager manager = null;
    	if (managerType ==1) {
    		manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        } else if (managerType == 2) {
        	manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        } else if (managerType == 3) {
        	manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        } else {
            throw new IllegalArgumentException("Invalid runtime maanger type");
        }
    	if (waitOnStart) {
	    	// wait for the 2 seconds (default startup delay for quartz)
	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// do nothing
			}
    	}
    	return manager;
    }

    
    @Test(timeout=20000)
    public void testTimerStartManagerClose() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 3);
        QuartzSchedulerService additionalCopy = new QuartzSchedulerService();
        additionalCopy.initScheduler(null);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                timerExporations.add(event.getProcessInstance().getId());                
            }

        };
        
        environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/TimerStart2.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener))
                .get();
        
        manager = getManager(environment, false);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        assertEquals(0, timerExporations.size());
       
        countDownListener.waitTillCompleted();
        manager.disposeRuntimeEngine(runtime);
        int atDispose = timerExporations.size();
        assertTrue(atDispose > 0);
        
        ((AbstractRuntimeManager)manager).close(true);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted(3000);
        assertEquals(atDispose, timerExporations.size());
        additionalCopy.shutdown();
    }
    
    
    
    /**
     * Test that illustrates that jobs are persisted and survives server restart
     * and as soon as GlobalTimerService is active jobs are fired and it loads and aborts the 
     * process instance to illustrate jobs are properly removed when isntance is aborted
     * NOTE: this test is disabled by default as it requires real db (not in memory)
     * and test to be executed separately each with new jvm process
     */
    @Test 
    @Ignore
    public void testAbortGlobalTestService() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .addConfiguration("drools.timerService", "org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate")
                .get();
        
        RuntimeManager manger = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        
        // build GlobalTimerService instance
        
        TimerService globalTs = new GlobalTimerService(manger, globalScheduler);
        // and register it in the registry under 'default' key
        TimerServiceRegistry.getInstance().registerTimerService("default", globalTs);
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
        long id = -1;
        Thread.sleep(5000);
        RuntimeEngine runtime = manger.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        ksession.addEventListener(listener);
        
        ksession.abortProcessInstance(id);
        ProcessInstance processInstance = ksession.getProcessInstance(id);        
        assertNull(processInstance);
        // let's wait to ensure no more timers are expired and triggered
        Thread.sleep(3000);
        ksession.dispose();
        
    }
    
    /**
     * Test that illustrates that jobs are persisted and survives server restart
     * and as soon as GlobalTimerService is active jobs are fired
     * NOTE: this test is disabled by default as it requires real db (not in memory)
     * and test to be executed separately each with new jvm process
     */
    @Test
    @Ignore
    public void testContinueGlobalTestService() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle2.bpmn2"), ResourceType.BPMN2)
                .addConfiguration("drools.timerService", "org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate")
                .get();
       
        RuntimeManager manger = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        
        // build GlobalTimerService instance
        
        TimerService globalTs = new GlobalTimerService(manger, globalScheduler);
        // and register it in the registry under 'default' key
        TimerServiceRegistry.getInstance().registerTimerService("default", globalTs);
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


        Thread.sleep(5000);
        
    }

    @Test(timeout=20000)
    public void testContinueTimer() throws Exception {
        // JBPM-4443
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
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

        // No special configuration for TimerService in order to test RuntimeManager default
        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle4.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener))
                .get();
        manager = getManager(environment, true);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        manager.disposeRuntimeEngine(runtime);
        
        countDownListener.waitTillCompleted();
        
        manager.close();

        countDownListener.reset(1);
        // ---- restart ----

        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle4.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        manager = getManager(environment, true);
        manager.disposeRuntimeEngine(runtime);
        
        countDownListener.waitTillCompleted(3000);
        assertEquals(2, timerExporations.size());
    }
    
    @Test(timeout=20000)
    public void testTimerRequiresRecoveryFlagSet() throws Exception {
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/HumanTaskWithBoundaryTimer.bpmn"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .userGroupCallback(userGroupCallback)
                .get();

        manager = getManager(environment, true);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "john");
        ProcessInstance processInstance = ksession.startProcess("PROCESS_1", params);

        Connection connection = null;
        Statement stmt = null;
        try {
            connection = ((DataSource)InitialContext.doLookup("jdbc/jbpm-ds")).getConnection();
            stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select REQUESTS_RECOVERY from QRTZ_JOB_DETAILS");
            while(resultSet.next()) {
                boolean requestsRecovery = resultSet.getBoolean(1);
                assertEquals("Requests recovery must be set to true", true, requestsRecovery);
            }
        } finally {
            if(stmt != null) {
                stmt.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
        ksession.abortProcessInstance(processInstance.getId());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test(timeout=25000)
    public void testContinueTimerWithMisfire() throws Exception {
        // RHBPMS-4729
        System.setProperty("org.quartz.properties", "quartz-db-short-misfire.properties");

        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 2);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){
            @Override
          public void beforeProcessStarted(ProcessStartedEvent event) {
              timerExporations.add(event.getProcessInstance().getId());
          }
        };

        // No special configuration for TimerService in order to test RuntimeManager default
        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/TimerStart2.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener))
                .get();
        manager = getManager(environment, true);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        countDownListener.waitTillCompleted();

        manager.disposeRuntimeEngine(runtime);
        manager.close();

        System.out.println("==== manager.close() ====");

        countDownListener.reset(3);

        // Simulate interval between shutdown and start so the Trigger is older than (now - misfireThreshold)
        Thread.sleep(5000);

        // ---- restart ----
        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/TimerStart2.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener))
                .get();
        manager = getManager(environment, true);

        countDownListener.waitTillCompleted(4000);

        assertEquals(5, timerExporations.size());
    }

}
