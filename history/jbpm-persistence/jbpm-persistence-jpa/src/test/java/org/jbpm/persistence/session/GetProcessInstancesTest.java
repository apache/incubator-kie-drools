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

package org.jbpm.persistence.session;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.persistence.processinstance.JPAProcessInstanceManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This test looks at the behavior of the  {@link JPAProcessInstanceManager} 
 * with regards to created (but not started) process instances 
 * and whether the process instances are available or not after creation.
 */
@RunWith(Parameterized.class)
public class GetProcessInstancesTest extends AbstractBaseTest {
    
    private HashMap<String, Object> context;
    
    private Environment env;
    private KieBase kbase;
    private long sessionId;
    
    public GetProcessInstancesTest(boolean locking) { 
       this.useLocking = locking; 
    }
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };
    
    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);

        kbase = createBase();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        sessionId = ksession.getIdentifier();
        ksession.dispose();
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void getEmptyProcessInstances() throws Exception {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
    }

    @Test
    public void create2ProcessInstances() throws Exception {
        long[] processId = new long[2];

        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        ksession.dispose();

        assertProcessInstancesExist(processId);
    }

    @Test
    public void create2ProcessInstancesInsideTransaction() throws Exception {
        long[] processId = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        assertEquals(2, ksession.getProcessInstances().size());
        
        // process instance manager cache flushed on tx
        ut.commit();
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesExist(processId);
    }

    @Test
    public void noProcessInstancesLeftAfterRollback() throws Exception {
        long[] notProcess = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        assertEquals(2, ksession.getProcessInstances().size());
        
        ut.rollback();
        // Validate that proc inst mgr cache is also flushed on rollback
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesNotExist(notProcess);
    }

    @Test
    public void noProcessInstancesLeftWithPreTxKSessionAndRollback() throws Exception {
        long[] notProcess = new long[4];

        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        
        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        
        ut.rollback();
        // Validate that proc inst mgr cache is also flushed on rollback
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesNotExist(notProcess);
    }
    
    @Test
    public void createProcessInstanceAndGetStartDate() throws Exception {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        long  processId= ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        assertEquals(0, ksession.getProcessInstances().size());
        
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) ksession.getProcessInstance(processId); 
        assertNotNull("Process " + processId + " exist!", processInstance);
        assertNotNull("Process start at " + processInstance.getStartDate(), processInstance.getStartDate());
        
        ksession.dispose();
        
    }

   
    /**
     * Helper functions
     */
    
    private void assertProcessInstancesExist(long[] processId) {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();

        for (long id : processId) {
            assertNotNull("Process instance " + id + " should not exist!", ksession.getProcessInstance(id));
        }
    }

    private void assertProcessInstancesNotExist(long[] processId) {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();

        for (long id : processId) {
            assertNull(ksession.getProcessInstance(id));
        }
    }

    private KieBase createBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
        assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());

        return kbuilder.newKieBase();
    }
    
    private StatefulKnowledgeSession reloadKnowledgeSession() {
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
    }

    private StatefulKnowledgeSession reloadKnowledgeSession(StatefulKnowledgeSession ksession) {
        ksession.dispose();
        return reloadKnowledgeSession();
    }
}
