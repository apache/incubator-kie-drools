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

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener;
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

public class AdHocSubprocessAbortRuntimeManagerTest extends AbstractBaseTest {
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private EntityManagerFactory emf;
    private RuntimeManager manager;
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
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }
    
    @Test
    public void testSingletonRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");        
        assertNotNull(manager);
        testAdHocSubprocess();
    }
    
    @Test
    public void testPerProcessInstanceRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");        
        assertNotNull(manager); 
        testAdHocSubprocess();
    }
   
    
    @Test
    public void testPerRequestRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");        
        assertNotNull(manager);
        testAdHocSubprocess();
    }
    
    @Test
    public void testPerCaseRuntimeManagerScopeSignal() {
        RuntimeEnvironment environment = createEnvironment();        
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment, "first");        
        assertNotNull(manager);
        testAdHocSubprocess();
    }

    private void testAdHocSubprocess() {
            
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        ProcessInstance processInstance = ksession1.startProcess("jbpm-abort-ht-issue.ad-hoc-abort-ht");
        manager.disposeRuntimeEngine(runtime1);
       

        // then signal via first manager, should only signal instances owned by that manager
        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession1 = runtime1.getKieSession();
        ksession1.addEventListener(new TaskCleanUpProcessEventListener(runtime1.getTaskService()));
        ksession1.signalEvent("Milestone", null, processInstance.getId());
        manager.disposeRuntimeEngine(runtime1);
        
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        // process instance 1 should be completed by signal
        ProcessInstanceLog pi1Log = auditService.findProcessInstance(processInstance.getId());
        assertNotNull(pi1Log);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi1Log.getStatus().intValue());
        
        auditService.dispose();
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    
    private RuntimeEnvironment createEnvironment() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("adhoc-subprocess.bpmn2"), ResourceType.BPMN2)
                .get();
        
        return environment;
    }
}
