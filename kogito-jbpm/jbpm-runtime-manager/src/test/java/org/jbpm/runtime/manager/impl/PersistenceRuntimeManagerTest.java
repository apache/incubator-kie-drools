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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.persistence.info.SessionInfo;
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
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class PersistenceRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager; 
    @Before
    public void setup() {
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        pds = TestUtil.setupPoolingDataSource();
    }
    
    @After
    public void teardown() {
        manager.close();
        pds.close();
    }
   
    @SuppressWarnings("unchecked")
    @Test
    public void testPerProcessInstanceManagerDestorySession() {
    	
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        EntityManager em = emf.createEntityManager();
        List<SessionInfo> sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);        
        assertNotNull(manager);
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        runtime.getKieSession();
        
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(1, sessions.size());
        
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        manager.disposeRuntimeEngine(runtime);
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(1, sessions.size());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        manager.disposeRuntimeEngine(runtime);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId())).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }      
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        manager.close();
        emf.close();
    }
   
    @SuppressWarnings("unchecked")
	@Test
    public void testPerRequestManagerDestorySession() {
    	
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        EntityManager em = emf.createEntityManager();
        List<SessionInfo> sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        runtime.getKieSession();
        
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(1, sessions.size());
        
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       

        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        manager.disposeRuntimeEngine(runtime);
        
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(1, sessions.size());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);

        manager.disposeRuntimeEngine(runtime);
        sessions = em.createQuery("from SessionInfo").getResultList();
        assertEquals(0, sessions.size());
        manager.close();
        emf.close();
    }
}
