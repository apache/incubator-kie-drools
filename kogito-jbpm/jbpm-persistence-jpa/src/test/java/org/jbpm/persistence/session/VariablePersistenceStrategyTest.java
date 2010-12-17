package org.jbpm.persistence.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.io.impl.ClassPathResource;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.impl.WorkImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessContext;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.JbpmTestCase;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowProcess;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class VariablePersistenceStrategyTest extends JbpmTestCase {

    private static Logger logger = LoggerFactory.getLogger( VariablePersistenceStrategyTest.class );
    
    private PoolingDataSource ds1;
    private EntityManagerFactory emf;

    @Before
    public void setUp() throws Exception {
        ds1 = new PoolingDataSource();
        ds1.setUniqueName( "jdbc/testDS1" );
        ds1.setClassName( "org.h2.jdbcx.JdbcDataSource" );
        ds1.setMaxPoolSize( 3 );
        ds1.setAllowLocalTransactions( true );
        ds1.getDriverProperties().put( "user",
                                       "sa" );
        ds1.getDriverProperties().put( "password",
                                       "sasa" );
        ds1.getDriverProperties().put( "URL",
                                       "jdbc:h2:mem:mydb" );
        ds1.init();
        
        emf = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");

    }

    @After
    public void tearDown() throws Exception {
        emf.close();
        ds1.close();
    }

    @Test
    public void testExtendingInterfaceVariablePersistence(){
        Environment env = createEnvironment();
        String processId = "extendingInterfaceVariablePersistence";
        String variableText = "my extending serializable variable text";
        KnowledgeBase kbase = getKnowledgeBaseForExtendingInterfaceVariablePersistence(processId,
                                                                                       variableText);
        StatefulKnowledgeSession ksession = createSession( kbase , env );
        Map<String, Object> initialParams = new HashMap<String, Object>();
        initialParams.put( "x", new MyVariableExtendingSerializable( variableText ) );
        long processInstanceId = ksession.startProcess( processId, initialParams ).getId();
        ksession = reloadSession( ksession, kbase, env );
        
        long workItemId = TestWorkItemHandler.getInstance().getWorkItem().getId();
        ksession.getWorkItemManager().completeWorkItem( workItemId, null );
        
        Assert.assertNull( ksession.getProcessInstance( processInstanceId ) );
    }

    private KnowledgeBase getKnowledgeBaseForExtendingInterfaceVariablePersistence(String processId, final String variableText) {
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
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        return kbase;
    }
    
    @Test
    public void testPersistenceVariables() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
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
        Environment env =  createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase( "VariablePersistenceStrategyProcess.rf" );
        StatefulKnowledgeSession ksession = createSession( kbase, env );

       
       
        
        
        logger.info("### Starting process ###");
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
        
        List<?> result = emf.createEntityManager().createQuery("select i from MyEntity i").getResultList();
        assertEquals(1, result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityMethods i").getResultList();
        assertEquals(1, result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityOnlyFields i").getResultList();
        assertEquals(1, result.size());

        logger.info("### Retrieving process instance ###");
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
        logger.info("### Completing first work item ###");
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        

        
        logger.info("### Retrieving process instance ###");
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
        logger.info("### Completing second work item ###");
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        

        logger.info("### Retrieving process instance ###");
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
        logger.info("### Completing third work item ###");
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
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase( "VariablePersistenceStrategyProcessTypeChange.rf" );
        StatefulKnowledgeSession ksession = createSession( kbase, env );
        
        
        
        
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
        KnowledgeBase kbase = createKnowledgeBase( "VariablePersistenceStrategySubProcess.rf" );
        StatefulKnowledgeSession ksession = createSession( kbase, env );
       
        
        
        
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
        KnowledgeBase kbase = createKnowledgeBase( "VPSProcessWithWorkItems.rf" );
        StatefulKnowledgeSession ksession = createSession( kbase , env);
        
        
       
       
        
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess( "com.sample.ruleflow", parameters ).getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        logger.info("### Retrieving process instance ###");
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

        logger.info("### Completing first work item ###");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x")+"->modifiedResult");

        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),  results );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        logger.info("### Retrieving process instance ###");
        ksession = reloadSession( ksession, kbase, env );
		processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstanceId);
		assertNotNull(processInstance);
        logger.info("######## Getting the already Persisted Variables #########");
        assertEquals("SomeString->modifiedResult", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        logger.info("### Completing second work item ###");
        results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x"));
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),  results );


        workItem = handler.getWorkItem();
        assertNotNull(workItem);

        logger.info("### Retrieving process instance ###");
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
        logger.info("### Completing third work item ###");
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
    	assertEquals(3L, JPAPlaceholderResolverStrategy.getClassIdValue(subEntity));
    }
    
    @Test
    public void testEntityWithSuperClassAnnotationMethod() throws Exception {
    	MySubEntityMethods subEntity = new MySubEntityMethods();
    	subEntity.setId(3L);
    	assertEquals(3L, JPAPlaceholderResolverStrategy.getClassIdValue(subEntity));
    }
    
    private StatefulKnowledgeSession createSession(KnowledgeBase kbase, Environment env){
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
    }
    
    private StatefulKnowledgeSession reloadSession(StatefulKnowledgeSession ksession, KnowledgeBase kbase, Environment env){
        int sessionId = ksession.getId();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env);
    }

    private KnowledgeBase createKnowledgeBase(String flowFile) {
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
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private Environment createEnvironment() {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
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