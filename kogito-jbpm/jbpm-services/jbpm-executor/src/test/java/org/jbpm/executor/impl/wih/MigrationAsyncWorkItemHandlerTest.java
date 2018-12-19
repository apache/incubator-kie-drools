/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.RequeueAware;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.impl.migration.MigrationException;
import org.jbpm.runtime.manager.impl.migration.MigrationManager;
import org.jbpm.runtime.manager.impl.migration.MigrationReport;
import org.jbpm.runtime.manager.impl.migration.MigrationSpec;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorManager;
import org.kie.test.util.db.PoolingDataSourceWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MigrationAsyncWorkItemHandlerTest extends AbstractExecutorBaseTest {

    private PoolingDataSourceWrapper pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private RuntimeManager manager2;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
    
    private EntityManagerFactory emfErrors = null;
    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
        
        emfErrors = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");
    }
    
    @After
    public void teardown() {
        executorService.destroy();
        if (manager != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        }
        if (manager2 != null) {
            RuntimeManagerRegistry.get().remove(manager2.getIdentifier());
            manager2.close();
        }
        if (emf != null) {
        	emf.close();
        }
        if (emfErrors != null) {
            emfErrors.close();
        }
        pds.close();
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }

    @Test(timeout=10000)
    public void testMigrateProcessWithAsyncHandler() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("KieDeploymentDescriptor",  new DeploymentDescriptorManager("org.jbpm.persistence.complete").getDefaultDescriptor())
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.test.MissingDataCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("KieDeploymentDescriptor",  new DeploymentDescriptorManager("org.jbpm.persistence.complete").getDefaultDescriptor())
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.test.MissingDataCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "version1"); 
        assertNotNull(manager);
        
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, "version2"); 
        assertNotNull(manager2);
        
        CountDownAsyncJobListener countDownExecutorListener = configureListener(1);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);                 
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        manager.disposeRuntimeEngine(runtime);
        
        countDownExecutorListener.waitTillCompleted();
        
        MigrationSpec migrationSpec = new MigrationSpec(manager.getIdentifier(), processInstance.getId(), manager2.getIdentifier(), "ScriptTask");
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        List<RequestInfo> executedRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
        
        assertEquals(manager2.getIdentifier(), executedRequests.get(0).getDeploymentId());
        
        Map<String, Object> fixedData = new HashMap<>();
        fixedData.put("amount", 200);
        
        executorService.updateRequestData(executedRequests.get(0).getId(), fixedData);
        countDownExecutorListener.reset(1);
        
        ((RequeueAware) executorService).requeueById(executedRequests.get(0).getId());        
        
        countDownExecutorListener.waitTillCompleted();        
        
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        
        AuditService auditService = runtime.getAuditService();
        
        ProcessInstanceLog log = auditService.findProcessInstance(processInstance.getId());
        assertEquals(manager2.getIdentifier(), log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());
        
        auditService.dispose();
        
        ksession = runtime.getKieSession();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
                
        manager2.disposeRuntimeEngine(runtime);
    }
    
    @Test(timeout=10000)
    public void testMigrateProcessWithAsyncHandlerNotAllowed() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithParams.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("KieDeploymentDescriptor",  new DeploymentDescriptorManager("org.jbpm.persistence.complete").getDefaultDescriptor())
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithParams.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("KieDeploymentDescriptor",  new DeploymentDescriptorManager("org.jbpm.persistence.complete").getDefaultDescriptor())
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "version1"); 
        assertNotNull(manager);
        
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, "version2"); 
        assertNotNull(manager2);
        
        CountDownAsyncJobListener countDownExecutorListener = configureListener(1);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);                 
        
        Map<String, Object> params = new HashMap<>();
        params.put("delayAsync", "5s");
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        manager.disposeRuntimeEngine(runtime);
                
                
        MigrationReport report = null;              
        try {
            MigrationSpec migrationSpec = new MigrationSpec(manager.getIdentifier(), processInstance.getId(), manager2.getIdentifier(), "ScriptTask");
            
            MigrationManager migrationManager = new MigrationManager(migrationSpec);
            migrationManager.migrate();
            fail("Migration should not be allowed for active jobs");
        } catch (MigrationException e) {
           report = e.getReport(); 
           assertEquals("There are active async jobs for process instance " + processInstance.getId() + " migration not allowed with active jobs", e.getMessage());
        } 
        
        assertNotNull(report);
        assertFalse(report.isSuccessful());        
                
        countDownExecutorListener.waitTillCompleted();        
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        
        AuditService auditService = runtime.getAuditService();
        
        ProcessInstanceLog log = auditService.findProcessInstance(processInstance.getId());
        assertEquals(manager.getIdentifier(), log.getExternalId());
        assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());
        
        auditService.dispose();
        
        ksession = runtime.getKieSession();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
                
        manager.disposeRuntimeEngine(runtime);
    }
    
    
    private ExecutorService buildExecutorService() {        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setRetries(0);
        executorService.init();
        
        return executorService;
    }
}
