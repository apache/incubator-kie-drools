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

package org.jbpm.persistence.processinstance;

import static org.kie.api.runtime.EnvironmentName.*;
import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.transaction.UserTransaction;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.jbpm.persistence.processinstance.objects.NonSerializableClass;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class ProcessInstanceResolverStrategyTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceResolverStrategyTest.class);
    
    private HashMap<String, Object> context;
    private StatefulKnowledgeSession ksession;

    private static final String RF_FILE = "SimpleProcess.rf";
    private final static String PROCESS_ID = "org.jbpm.persistence.TestProcess";
    private final static String VAR_NAME = "persistVar";
    
    public ProcessInstanceResolverStrategyTest(boolean locking) { 
       this.useLocking = locking; 
    }
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };
   
    @Before
    public void before() { 
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        
        // load up the knowledge base
        Environment env = PersistenceUtil.createEnvironment(context);
        env.set(OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[] {
                new ProcessInstanceResolverStrategy(),
                new JPAPlaceholderResolverStrategy(env),
                new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) }
                );
        if( useLocking ) { 
            env.set(USE_PESSIMISTIC_LOCKING, true);
        }
        KieBase kbase = loadKnowledgeBase();

        // create session
        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        Assert.assertTrue("Valid KnowledgeSession could not be created.", ksession != null && ksession.getIdentifier() > 0);
    }
    
    private KieBase loadKnowledgeBase() { 
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( RF_FILE ), ResourceType.DRF );
        KieBase kbase = kbuilder.newKieBase();
        return kbase;
    }
    
    
    @After
    public void after() {
        if( ksession != null ) { 
            ksession.dispose();
        }
        cleanUp(context);
    }

    @Test
    public void testWithDatabaseAndStartProcess() throws Exception {
        // Create variable
        Map<String, Object> params = new HashMap<String, Object>();
        NonSerializableClass processVar = new NonSerializableClass();
        processVar.setString("1234567890");
        params.put(VAR_NAME, processVar);

        // Persist variable
        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        EntityManagerFactory emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        em.joinTransaction();
        em.persist(processVar);
        em.close();
        ut.commit();

        // Generate, insert, and start process
        ProcessInstance processInstance = ksession.startProcess(PROCESS_ID, params);

        // Test resuls
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processVar = (NonSerializableClass) ((WorkflowProcessInstance) processInstance).getVariable(VAR_NAME);
        Assert.assertNotNull(processVar);
    }

    @Test
    public void testWithDatabaseAndStartProcessInstance() throws Exception {
        // Create variable
        Map<String, Object> params = new HashMap<String, Object>();
        NonSerializableClass processVar = new NonSerializableClass();
        processVar.setString("1234567890");
        params.put(VAR_NAME, processVar);
    
        // Persist variable
        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        EntityManagerFactory emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        em.joinTransaction();
        em.persist(processVar);
        em.close();
        ut.commit();
    
        // Create process,
        ProcessInstance processInstance = ksession.createProcessInstance(PROCESS_ID, params);
        long processInstanceId = processInstance.getId();
        Assert.assertTrue(processInstanceId > 0);
        Assert.assertEquals(ProcessInstance.STATE_PENDING, processInstance.getState());
        
        // insert process,
        ksession.insert(processInstance);
   
        // and start process
        ksession.startProcessInstance(processInstanceId);
        ksession.fireAllRules();
    
        // Test results
        processInstance = ksession.getProcessInstance(processInstanceId);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processVar = (NonSerializableClass) ((WorkflowProcessInstance) processInstance).getVariable(VAR_NAME);
        Assert.assertNotNull(processVar);
    }

}
