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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.jbpm.persistence.session.objects.MyEntity;
import org.jbpm.persistence.session.objects.MyEntityMethods;
import org.jbpm.persistence.session.objects.MyEntityOnlyFields;
import org.jbpm.persistence.session.objects.MySubEntity;
import org.jbpm.persistence.session.objects.MySubEntityMethods;
import org.jbpm.persistence.session.objects.MyVariableExtendingSerializable;
import org.jbpm.persistence.session.objects.MyVariableSerializable;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class VariablePersistenceStrategyTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger( VariablePersistenceStrategyTest.class );
    
    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    
    public VariablePersistenceStrategyTest(boolean locking) { 
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
        emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void testExtendingInterfaceVariablePersistence() throws Exception {
        // Setup
        Environment env = createEnvironment();
        String processId = "extendingInterfaceVariablePersistence";
        String variableText = "my extending serializable variable text";
        KieBase kbase = getKnowledgeBaseForExtendingInterfaceVariablePersistence(processId,
                                                                                       variableText);
        KieSession ksession = createSession( kbase , env );
        Map<String, Object> initialParams = new HashMap<String, Object>();
        initialParams.put( "x", new MyVariableExtendingSerializable( variableText ) );
        
        // Start process and execute workItem
        long processInstanceId = ksession.startProcess( processId, initialParams ).getId();
        
        ksession = reloadSession( ksession, kbase, env );
        
        long workItemId = TestWorkItemHandler.getInstance().getWorkItem().getId();
        ksession.getWorkItemManager().completeWorkItem( workItemId, null );
        
        // Test
        Assert.assertNull( ksession.getProcessInstance( processInstanceId ) );
    }

    private KieBase getKnowledgeBaseForExtendingInterfaceVariablePersistence(String processId, final String variableText) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( processId );
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("x");
        ObjectDataType extendingSerializableDataType = new ObjectDataType();
        extendingSerializableDataType.setClassName(MyVariableExtendingSerializable.class.getName());
        variable.setType(extendingSerializableDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName( "Start" );
        startNode.setId(1);

        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName( "workItemNode" );
        workItemNode.setId( 2 );
        Work work = new WorkImpl();
        work.setName( "MyWork" );
        workItemNode.setWork( work );
        
        ActionNode actionNode = new ActionNode();
        actionNode.setName( "Print" );
        DroolsAction action = new DroolsConsequenceAction( "java" , null);
        action.setMetaData( "Action" , new Action() {
            public void execute(ProcessContext context) throws Exception {
                Assert.assertEquals( variableText , ((MyVariableExtendingSerializable) context.getVariable( "x" )).getText()); ;
            }
        });
        actionNode.setAction(action);
        actionNode.setId( 3 );
        
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);
        
        connect( startNode, workItemNode );
        connect( workItemNode, actionNode );
        connect( actionNode, endNode );

        process.addNode( startNode );
        process.addNode( workItemNode );
        process.addNode( actionNode );
        process.addNode( endNode );
        
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((KnowledgeBaseImpl) kbase).addProcess(process);
        return kbase;
    }
    
    @Test
    public void testPersistenceVariables() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        if( utx.getStatus() == Status.STATUS_NO_TRANSACTION ) { 
            utx.begin();
            em.joinTransaction();
        }
        int origNumMyEntities = em.createQuery("select i from MyEntity i").getResultList().size();
        int origNumMyEntityMethods = em.createQuery("select i from MyEntityMethods i").getResultList().size();
        int origNumMyEntityOnlyFields = em.createQuery("select i from MyEntityOnlyFields i").getResultList().size();
        if( utx.getStatus() == Status.STATUS_ACTIVE ) { 
            utx.commit();
        }
       
        // Setup entities
        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");

        // persist entities
        utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        utx.commit();
        em.close();
        
        // More setup
        Environment env =  createEnvironment();
        KieBase kbase = createKnowledgeBase( "VariablePersistenceStrategyProcess.rf" );
        KieSession ksession = createSession( kbase, env );

        logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods);
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        
        // Start process
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        
        // Test results
        List<?> result = emf.createEntityManager().createQuery("select i from MyEntity i").getResultList();
        assertEquals(origNumMyEntities + 1, result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityMethods i").getResultList();
        assertEquals(origNumMyEntityMethods + 1, result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityOnlyFields i").getResultList();
        assertEquals(origNumMyEntityOnlyFields + 1, result.size());

        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env );
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
        	ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
        logger.debug("### Completing first work item ###");
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        

        
        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase , env );
		processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstanceId);
		assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        logger.debug("### Completing second work item ###");
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        

        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env);
        processInstance = (WorkflowProcessInstance)
        	ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some changed String", processInstance.getVariable("a"));
        assertEquals("This is a changed test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a changed test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        logger.debug("### Completing third work item ###");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        workItem = handler.getWorkItem();
        assertNull(workItem);
        

        ksession = reloadSession( ksession, kbase, env );
        processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstanceId);
        assertNull(processInstance);
    }
    
    @Test
    public void testPersistenceVariablesWithTypeChange() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");

        EntityManager em = emf.createEntityManager();
        UserTransaction utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        int s = utx.getStatus();
        if( utx.getStatus() == Status.STATUS_NO_TRANSACTION ) { 
            utx.begin();
        }
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        if( utx.getStatus() == Status.STATUS_ACTIVE ) { 
            utx.commit();
        }
        em.close();
        Environment env = createEnvironment();
        KieBase kbase = createKnowledgeBase( "VariablePersistenceStrategyProcessTypeChange.rf" );
        KieSession ksession = createSession( kbase, env );
        
        
        
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods );
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        ProcessInstance processInstance = ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        processInstance = ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        processInstance = ksession.getProcessInstance( processInstanceId );
        assertNull( processInstance );
    }
    
    @Test
    public void testPersistenceVariablesSubProcess() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        
        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KieBase kbase = createKnowledgeBase( "VariablePersistenceStrategySubProcess.rf" );
        KieSession ksession = createSession( kbase, env );
       
        
        
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods);
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        ProcessInstance processInstance = ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        processInstance = ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        processInstance = ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = reloadSession( ksession, kbase, env );
        processInstance = ksession.getProcessInstance( processInstanceId );
        assertNull( processInstance );
    }
    
    @Test
    public void testWorkItemWithVariablePersistence() throws Exception{
        MyEntity myEntity = new MyEntity("This is a test Entity");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        utx.begin();
        
        em.joinTransaction();
        em.persist(myEntity);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KieBase kbase = createKnowledgeBase( "VPSProcessWithWorkItems.rf" );
        KieSession ksession = createSession( kbase , env);
        
        
       
       
        
        logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase , env);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
        	ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));

        logger.debug("### Completing first work item ###");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x")+"->modifiedResult");

        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),  results );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env );
		processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstanceId);
		assertNotNull(processInstance);
        logger.debug("######## Getting the already Persisted Variables #########");
        assertEquals("SomeString->modifiedResult", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        logger.debug("### Completing second work item ###");
        results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x"));
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),  results );


        workItem = handler.getWorkItem();
        assertNotNull(workItem);

        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env );
        processInstance = (WorkflowProcessInstance)
        	ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        assertEquals("SomeString->modifiedResult", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some changed String", processInstance.getVariable("a"));
        assertEquals("This is a changed test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a changed test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        logger.debug("### Completing third work item ###");
        results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x"));
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),  results );

        workItem = handler.getWorkItem();
        assertNull(workItem);


        ksession = reloadSession( ksession, kbase, env );
        processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstanceId);
        assertNull(processInstance);
    }

    @Test
    public void testEntityWithSuperClassAnnotationField() throws Exception {
    	MySubEntity subEntity = new MySubEntity();
    	subEntity.setId(3L);
    	assertEquals(3L, (long) emf.getPersistenceUnitUtil().getIdentifier(subEntity));
    }
    
    @Test
    public void testEntityWithSuperClassAnnotationMethod() throws Exception {
    	MySubEntityMethods subEntity = new MySubEntityMethods();
    	subEntity.setId(3L);
    	assertEquals(3L, (long) emf.getPersistenceUnitUtil().getIdentifier(subEntity));
    }
    
    @Test
    public void testAbortWorkItemWithVariablePersistence() throws Exception{
        MyEntity myEntity = new MyEntity("This is a test Entity");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        utx.begin();
        
        em.joinTransaction();
        em.persist(myEntity);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KieBase kbase = createKnowledgeBase( "VPSProcessWithWorkItems.rf" );
        KieSession ksession = createSession( kbase , env);
        
        logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();
    
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
    
        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase , env);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
               ksession.getProcessInstance( processInstanceId );
        assertNotNull( processInstance );
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
    
        logger.debug("### Completing first work item ###");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x")+"->modifiedResult");
    
        // we simulate a failure here, aborting the work item
        ksession.getWorkItemManager().abortWorkItem( workItem.getId() );
    
        workItem = handler.getWorkItem();
        assertNotNull( workItem );
    
        logger.debug("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env );
               processInstance = (WorkflowProcessInstance)
                       ksession.getProcessInstance(processInstanceId);
               assertNotNull(processInstance);
        logger.debug("######## Getting the already Persisted Variables #########");
        // we expect the variables to be unmodifed
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
    }    
    
    private KieSession createSession(KieBase kbase, Environment env){
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
    }
    
    private KieSession reloadSession(KieSession ksession, KieBase kbase, Environment env){
        long sessionId = ksession.getIdentifier();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env);
    }

    private KieBase createKnowledgeBase(String flowFile) {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setProperty("drools.dialect.java.compiler", "JANINO");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add( new ClassPathResource( flowFile ), ResourceType.DRF );
        if(kbuilder.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (KnowledgeBuilderError error: kbuilder.getErrors()) {
                errorMessage.append( error.getMessage() );
                errorMessage.append( System.getProperty( "line.separator" ) );
            }
            fail( errorMessage.toString());
        }
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private Environment createEnvironment() {
        Environment env = PersistenceUtil.createEnvironment(context);
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                                    new JPAPlaceholderResolverStrategy(env),
                                    new SerializablePlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT  )
                                     });
        return env;
    }
    
    private void connect(Node sourceNode,
                         Node targetNode) {
        new ConnectionImpl (sourceNode, Node.CONNECTION_DEFAULT_TYPE,
                            targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }

}
