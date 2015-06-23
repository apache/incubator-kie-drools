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

package org.jbpm.test.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
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
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

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

    
    @Test
    public void testTimerStartManagerClose() throws Exception {
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
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-TimerStart2.bpmn2"), ResourceType.BPMN2)
                .schedulerService(globalScheduler)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        
        manager = getManager(environment, false);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        
        assertEquals(0, timerExporations.size());
       
        Thread.sleep(3600);
        manager.disposeRuntimeEngine(runtime);
        ((AbstractRuntimeManager)manager).close(true);
        Thread.sleep(3000);
        assertEquals(3, timerExporations.size());
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
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
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
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2"), ResourceType.BPMN2)
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

    @Test
    public void testContinueTimer() throws Exception {
        // JBPM-4443

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
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle4.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        manager = getManager(environment, true);

        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");

        Thread.sleep(5000); // Make sure a Timer is triggered once

        assertEquals(1, timerExporations.size());

        manager.disposeRuntimeEngine(runtime);
        manager.close();

        Thread.sleep(5000); // Wait so the timer gets overdue

        // ---- restart ----

        environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle4.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listener))
                .get();
        manager = getManager(environment, true);

        Thread.sleep(2000);

        assertEquals(2, timerExporations.size());

        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }
}
