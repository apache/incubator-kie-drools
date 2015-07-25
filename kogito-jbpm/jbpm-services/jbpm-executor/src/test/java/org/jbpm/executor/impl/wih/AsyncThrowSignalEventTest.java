/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.ExecutorService;
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
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AsyncThrowSignalEventTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
    }
    
    @After
    public void teardown() {
        executorService.destroy();
        if (manager != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        }
        if (emf != null) {
        	emf.close();
        }
        pds.close();
    }

    @Test
    public void testAsyncThrowEndEvent() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ThrowEventEnd.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        ProcessInstance processInstanceThrow = ksession.startProcess("SendEvent");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceThrow.getState());
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    } 
    
    @Test
    public void testAsyncThrowIntermediateEvent() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ThrowEventIntermediate.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        ProcessInstance processInstanceThrow = ksession.startProcess("SendIntermediateEvent");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceThrow.getState());
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    } 
    
    @Test
    public void testAsyncThrowManualEvent() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        ksession.signalEvent("ASYNC-MySignal", null);

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    } 
    
    private ExecutorService buildExecutorService() {        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        
        executorService.init();
        
        return executorService;
    }
}
