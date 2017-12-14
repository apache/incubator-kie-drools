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

package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
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
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

public class SignalScopedToRuntimeManagerTest extends AbstractBaseTest {
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private EntityManagerFactory emf;
    private RuntimeManager manager;
    private RuntimeManager manager2;
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
    }
    
    @After
    public void teardown() {
        if (manager != null) {
            manager.close();
        }
        if (manager2 != null) {
            manager2.close();
        }
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }
    
    @Test
    public void testSingletonRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testSignalEventScopedToOwningRuntimeManager();
    }
    
    @Test
    public void testPerProcessInstanceRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testSignalEventScopedToOwningRuntimeManager();
    }
   
    
    @Test
    public void testPerRequestRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testSignalEventScopedToOwningRuntimeManager();
    }

    private void testSignalEventScopedToOwningRuntimeManager() {
        
        // start first process instance with first manager
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        ProcessInstance processInstance = ksession1.startProcess("IntermediateCatchEventWithRef");
        manager.disposeRuntimeEngine(runtime1);
        // start another process instance of the same process just owned by another manager
        RuntimeEngine runtime2 = manager2.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        assertNotNull(ksession2);         
        ProcessInstance processInstance2 = ksession2.startProcess("IntermediateCatchEventWithRef");
        manager2.disposeRuntimeEngine(runtime2);

        // then signal via first manager, should only signal instances owned by that manager
        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession1 = runtime1.getKieSession();
        ksession1.signalEvent("Signal1", "first");
        manager.disposeRuntimeEngine(runtime1);
        
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        // process instance 1 should be completed by signal
        ProcessInstanceLog pi1Log = auditService.findProcessInstance(processInstance.getId());
        assertNotNull(pi1Log);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi1Log.getStatus().intValue());
        // process instance 2 should still be active
        ProcessInstanceLog pi2Log = auditService.findProcessInstance(processInstance2.getId());
        assertNotNull(pi2Log);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2Log.getStatus().intValue());
        
        // then signal via second manager, should only signal instances owned by that manager
        runtime2 = manager2.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));
        ksession2 = runtime2.getKieSession();
        ksession2.signalEvent("Signal1", "second");
        manager2.disposeRuntimeEngine(runtime2);
        pi2Log = auditService.findProcessInstance(processInstance2.getId());
        assertNotNull(pi2Log);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi2Log.getStatus().intValue());
        
        auditService.dispose();
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testSingletonRuntimeManagerCompleteTaskViaWrongRuntimeManager() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testCompleteTaskViaWrongRuntimeManager();
    }
    
    @Test
    public void testPerProcessInstanceRuntimeManagerCompleteTaskViaWrongRuntimeManager() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testCompleteTaskViaWrongRuntimeManager();
    }
   
    
    @Test
    public void testPerRequestRuntimeManagerCompleteTaskViaWrongRuntimeManager() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");        
        assertNotNull(manager);
        
        RuntimeEnvironment environment2 = createEnvironment();        
        manager2 = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment2, "second");        
        assertNotNull(manager2);
        
        testCompleteTaskViaWrongRuntimeManager();
    }
    
    private void testCompleteTaskViaWrongRuntimeManager() {
        
        // start first process instance with first manager
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        ProcessInstance processInstance = ksession1.startProcess("UserTask");
        manager.disposeRuntimeEngine(runtime1);
        // start another process instance of the same process just owned by another manager
        RuntimeEngine runtime2 = manager2.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        assertNotNull(ksession2);         
        ProcessInstance processInstance2 = ksession2.startProcess("UserTask");
        manager2.disposeRuntimeEngine(runtime2);

        // then complete first instance task via second manager, should throw exception
        runtime2 = manager2.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));
        List<Long> tasks1 = runtime2.getTaskService().getTasksByProcessInstanceId(processInstance.getId());
        assertEquals(1, tasks1.size());
        
        long task1Id = tasks1.get(0);
        try {
            runtime2.getTaskService().start(task1Id, "john");
        
            runtime2.getTaskService().complete(task1Id, "john", null);
            fail("Should not be allowed to complete task via wrong runtime manager");
        } catch (IllegalStateException re) {
            assertEquals("Task instance " + task1Id + " is owned by another deployment expected first found second" , re.getMessage());
        }
        manager2.disposeRuntimeEngine(runtime2);        
        
        // close manager which will close session maintained by the manager
        manager2.close();
    }


    private RuntimeEnvironment createEnvironment() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventSignalWithRef.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        return environment;
    }
}
