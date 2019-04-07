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

package org.jbpm.runtime.manager.impl.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import javax.naming.NamingException;

import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.tx.NoOpTransactionManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

@RunWith(Parameterized.class)
public class InMemorySessionFactoryLeakTest extends AbstractBaseTest {
    
    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {"singleton"}, 
                 {"request"},
                 {"processinstance"},
                 {"case"}
           });
    }
    
    private String strategy;
       
    public InMemorySessionFactoryLeakTest(String strategy) {
        this.strategy = strategy;
    }
        
    private UserGroupCallback userGroupCallback;    
    private RuntimeManager manager;
    
    @Rule 
    public TestName testName = new TestName();
    
    @Before
    public void setup() throws NamingException {
        TestUtil.cleanupSingletonSessionId();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        createRuntimeManager();
    }
    
    @After
    public void teardown() {
        if (manager != null) {
            manager.close();
        }        
    }
    
    private void createRuntimeManager() {
        RuntimeEnvironment environment = createEnvironment();        
        if ("singleton".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");
        } else if ("processinstance".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");
        } else if ("request".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");
        } else if ("case".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment, "first");
        }
        assertNotNull(manager);        
    }
    
    @Test
    public void testInMemorySessionCleanup() {
            
        for ( int i = 0; i < 20; i++ ) {
            Context<?> context = getContext();
            RuntimeEngine runtime = manager.getRuntimeEngine(context);
            KieSession ksession = runtime.getKieSession();

            ProcessInstance pi = ksession.startProcess("ScriptTask");
            
            assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());
            manager.disposeRuntimeEngine( runtime );
            
            if ("case".equals(strategy)) {
                ((PerCaseRuntimeManager) manager).destroyCase((CaseContext) context);
            }
        }

        InMemorySessionFactory factory = (InMemorySessionFactory) ((AbstractRuntimeManager) manager).getFactory();

        int expectedSessionsInFactory = getExpectedSessionsInFactory();
        assertEquals(expectedSessionsInFactory, factory.getSessions().size());
        
    }
    
   
    private RuntimeEnvironment createEnvironment() {
        
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
                .newEmptyBuilder()
                .addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, new NoOpTransactionManager())
                .addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName())
                .addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName())            
                .registerableItemsFactory(new SimpleRegisterableItemsFactory())
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        return builder.get();
    }
  
    private int getExpectedSessionsInFactory() {
             
        if ("singleton".equals(strategy)) {
            return 1; // for singleton there is single session kept until runtime manager is disposed
        } else if ("processinstance".equals(strategy)) {
            return 0;
        } else if ("request".equals(strategy)) {
            return 0;
        } else if ("case".equals(strategy)) {
            return 0;
        }

        throw new IllegalStateException("Not supported strategy " + strategy);
    }
    
    private Context<?> getContext() {
        
        if ("case".equals(strategy)) {
            return CaseContext.get(UUID.randomUUID().toString());
        }

        return ProcessInstanceIdContext.get();
    }
}
