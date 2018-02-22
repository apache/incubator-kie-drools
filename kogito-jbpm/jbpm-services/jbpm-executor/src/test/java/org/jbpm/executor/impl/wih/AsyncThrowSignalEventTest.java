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

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
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
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }

    @Test(timeout=10000)
    public void testAsyncThrowEndEvent() throws Exception {

        CountDownAsyncJobListener countDownListener = configureListener(1);
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
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    } 
    
    @Test(timeout=10000)
    public void testAsyncThrowIntermediateEvent() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
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
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    } 
    
    @Test(timeout=10000)
    public void testAsyncThrowManualEvent() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
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
        
        countDownListener.waitTillCompleted();
        
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
