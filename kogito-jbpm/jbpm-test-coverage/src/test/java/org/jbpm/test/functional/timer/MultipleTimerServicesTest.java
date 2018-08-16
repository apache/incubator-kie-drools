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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This test is dedicated to quartz scheduler service as it is controlled
 * by  org.quartz.properties system property, runtime manager will be bootstrapped
 * based on this property if not given ThreadPoolSchedulerService will be used which is default 
 *
 */
public class MultipleTimerServicesTest extends TimerBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MultipleTimerServicesTest.class);

    private RuntimeEnvironment environmentM1;
    private RuntimeEnvironment environmentM2;
    
    private RuntimeManager managerM1;
    private RuntimeManager managerM2;
    
    private int managerType = 1;
    
    private EntityManagerFactory emf;
    private EntityManagerFactory emf2;
    
    protected GlobalSchedulerService globalScheduler1;
    protected GlobalSchedulerService globalScheduler2;
    
    protected RuntimeManager getManager(RuntimeEnvironment environment, String id) {
        if (managerType ==1) {
            return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, id);
        }  else if (managerType == 2) {
            return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, id);
        } else {
            throw new IllegalArgumentException("Invalid runtime maanger type");
        }
    }
    
    @Before
    public void setup() {
        Collection<String> runtimeManagerIds = RuntimeManagerRegistry.get().getRegisteredIdentifiers();
        if (runtimeManagerIds != null) {
            for (String id : runtimeManagerIds) {
                RuntimeManagerRegistry.get().remove(id);
            }
        }
        
        System.setProperty("org.quartz.properties", "quartz-db.properties");
        testCreateQuartzSchema();
        cleanupSingletonSessionId();
        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
        emf2 = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
        
        globalScheduler1 = new QuartzSchedulerService();
        globalScheduler2 = new QuartzSchedulerService();
        ((QuartzSchedulerService)globalScheduler1).forceShutdown();
        ((QuartzSchedulerService)globalScheduler2).forceShutdown();
    }
    
    @After
    public void cleanup() {
        System.clearProperty("org.quartz.properties");
        ((QuartzSchedulerService)globalScheduler1).forceShutdown();
        ((QuartzSchedulerService)globalScheduler2).forceShutdown();
        managerM1.close();
        managerM2.close();
        
        EntityManagerFactory emf = ((SimpleRuntimeEnvironment) environmentM1).getEmf();
        if (emf != null) {
            emf.close();
        }
        
        emf = ((SimpleRuntimeEnvironment) environmentM2).getEmf();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        
    }    
    
    @Test(timeout=60000)
    public void testGlobalTimerServiceOnIndependentProcessInstanceManager() throws Exception {
        managerType = 2;
        
        testGlobalTimerServiceOnIndependentManager();
    }
    
    public void testGlobalTimerServiceOnIndependentManager() throws Exception {

        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("timer", 3);
        
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listenerM1 = new DefaultProcessEventListener(){

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    logger.debug("On manager 1");
                    timerExporations.add(event.getProcessInstance().getId());
                }
            }
            
        };
        
        final List<Long> timerExporations2 = new ArrayList<Long>();
        ProcessEventListener listenerM2 = new DefaultProcessEventListener(){

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    logger.debug("On manager 2");
                    timerExporations2.add(event.getProcessInstance().getId());
                }
            }
            
        };
        
        environmentM1 = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listenerM1, countDownListener))
                .schedulerService(globalScheduler1)
                .get();

        environmentM2 = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
    			.entityManagerFactory(emf2)
                .addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new TestRegisterableItemsFactory(listenerM2, countDownListener2))
                .schedulerService(globalScheduler2)
                .get();
        
        managerM1 = getManager(environmentM1, "one");

        RuntimeEngine runtimeM1 = managerM1.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksessionM1 = runtimeM1.getKieSession();
        
        managerM2 = getManager(environmentM2, "two");

        RuntimeEngine runtimeM2 = managerM2.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksessionM2 = runtimeM2.getKieSession();
        
        
        ProcessInstance processInstanceM1 = ksessionM1.startProcess("IntermediateCatchEvent");
        assertTrue(processInstanceM1.getState() == ProcessInstance.STATE_ACTIVE);
        
        ProcessInstance processInstanceM2 = ksessionM2.startProcess("IntermediateCatchEvent");
        assertTrue(processInstanceM2.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for first timer to trigger
        countDownListener.waitTillCompleted(2000);
        countDownListener2.waitTillCompleted(2000);
        // dispose session to force session to be reloaded on timer expiration
        managerM1.disposeRuntimeEngine(runtimeM1);
        managerM2.disposeRuntimeEngine(runtimeM2);
        
        countDownListener.waitTillCompleted();
        countDownListener2.waitTillCompleted();
        
        countDownListener.reset(1);
        countDownListener2.reset(1);
        
        try {
            runtimeM1 = managerM1.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceM1.getId()));
            ksessionM1 = runtimeM1.getKieSession();
            
            processInstanceM1 = ksessionM1.getProcessInstance(processInstanceM1.getId());        
            assertNull(processInstanceM1);
        } catch (SessionNotFoundException e) {
            // expected in PerProcessInstance manager
        }
        try {
            runtimeM2 = managerM2.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceM2.getId()));
            ksessionM2 = runtimeM2.getKieSession();
            
            processInstanceM2 = ksessionM2.getProcessInstance(processInstanceM2.getId());        
            assertNull(processInstanceM2);
        } catch (SessionNotFoundException e) {
            // expected in PerProcessInstance manager
        }
        // let's wait to ensure no more timers are expired and triggered
        countDownListener.waitTillCompleted(3000);
        countDownListener2.waitTillCompleted(3000);
   

        managerM1.disposeRuntimeEngine(runtimeM1);
        managerM2.disposeRuntimeEngine(runtimeM2);
        
        assertEquals(3, timerExporations.size());
        assertEquals(3, timerExporations2.size());
        

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
