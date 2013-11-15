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

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.runtime.RuntimeManagerRegistry;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.executor.api.ExecutorAdminService;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AsyncWorkItemHandlerTest extends AbstractBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
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
        pds.close();
    }

    @Test
    public void testRunProcessWithAsyncHandler() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        // important to register RuntimeManager in the registry as callbacks rely on it to move process forward
        RuntimeManagerRegistry.get().addRuntimeManager(manager.getIdentifier(), manager);
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test
    public void testRunProcessWithAsyncHandlerWithAbort() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        // important to register RuntimeManager in the registry as callbacks rely on it to move process forward
        RuntimeManagerRegistry.get().addRuntimeManager(manager.getIdentifier(), manager);
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test
    public void testRunProcessWithAsyncHandlerDuplicatedRegister() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        // important to register RuntimeManager in the registry as callbacks rely on it to move process forward
        RuntimeManagerRegistry.get().addRuntimeManager(manager.getIdentifier(), manager);
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        manager.close();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        RuntimeManagerRegistry.get().addRuntimeManager(manager.getIdentifier(), manager);
    }
    
    private ExecutorService buildExecutorService() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        EntityManager em = emf.createEntityManager();
        
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(new JbpmLocalTransactionManager());        
        
        ExecutorService executorService = new ExecutorServiceImpl();       
        
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl();
        ((ExecutorQueryServiceImpl)queryService).setPm(pm);
        
        ((ExecutorServiceImpl)executorService).setQueryService(queryService);

        Executor executor = new ExecutorImpl();
        ClassCacheManager classCacheManager = new ClassCacheManager();
        ExecutorRunnable runnable = new ExecutorRunnable();
        runnable.setPm(pm);
        runnable.setQueryService(queryService);
        runnable.setClassCacheManager(classCacheManager);
        ((ExecutorImpl)executor).setPm(pm);
        ((ExecutorImpl)executor).setExecutorRunnable(runnable);
        ((ExecutorImpl)executor).setQueryService(queryService);
        ((ExecutorImpl)executor).setClassCacheManager(classCacheManager);
        
        ((ExecutorServiceImpl)executorService).setExecutor(executor);
        
        ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
        ((ExecutorRequestAdminServiceImpl)adminService).setPm(pm);
        ((ExecutorServiceImpl)executorService).setAdminService(adminService);
        executorService.init();
        
        return executorService;
    }
}
