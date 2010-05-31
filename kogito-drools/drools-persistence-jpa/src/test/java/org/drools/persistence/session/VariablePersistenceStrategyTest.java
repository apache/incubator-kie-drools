package org.drools.persistence.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.processinstance.VariablePersistenceStrategyFactory;
import org.drools.persistence.processinstance.persisters.JPAVariablePersister;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkflowProcessInstance;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class VariablePersistenceStrategyTest extends TestCase {

    PoolingDataSource ds1;

    @Override
    protected void setUp() throws Exception {
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
        VariablePersistenceStrategyFactory.getVariablePersistenceStrategy()
        	.setPersister("javax.persistence.Entity",
				"org.drools.persistence.processinstance.persisters.JPAVariablePersister");
        VariablePersistenceStrategyFactory.getVariablePersistenceStrategy()
	    	.setPersister("java.io.Serializable",
				"org.drools.persistence.processinstance.persisters.SerializableVariablePersister");
    }

    @Override
    protected void tearDown() throws Exception {
        ds1.close();
    }

    public void testPersistenceVariables() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "VariablePersistenceStrategyProcess.rf" ), ResourceType.DRF );
        for (KnowledgeBuilderError error: kbuilder.getErrors()) {
        	System.out.println(error);
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();

        System.out.println("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", new MyEntity("This is a test Entity with annotation in fields"));
        parameters.put("m", new MyEntityMethods("This is a test Entity with annotations in methods"));
        parameters.put("f", new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods"));
        parameters.put("z", new MyVariableSerializable("This is a test SerializableObject"));
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
        	ksession.startProcess( "com.sample.ruleflow", parameters );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        
        List<?> result = emf.createEntityManager().createQuery("select i from VariableInstanceInfo i").getResultList();
        assertEquals(5, result.size());

        System.out.println("### Retrieving process instance ###");
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = (WorkflowProcessInstance) 
        	ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
        System.out.println("### Completing first work item ###");
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        
        System.out.println("### Retrieving variable instance infos ###");
        result = emf.createEntityManager().createQuery("select i from VariableInstanceInfo i").getResultList();
        assertEquals(8, result.size());
        for (Object o: result) {
        	assertTrue(VariableInstanceInfo.class.isAssignableFrom(o.getClass()));
        	System.out.println(o);
        }
        
        System.out.println("### Retrieving process instance ###");
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
		processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstance.getId());
		assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        System.out.println("### Completing second work item ###");
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        result = emf.createEntityManager().createQuery("select i from VariableInstanceInfo i").getResultList();
        assertEquals(8, result.size());
        
        System.out.println("### Retrieving process instance ###");
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = (WorkflowProcessInstance)
        	ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) processInstance.getVariable("y")).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) processInstance.getVariable("m")).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) processInstance.getVariable("f")).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("z")).getText());
        assertEquals("Some changed String", processInstance.getVariable("a"));
        assertEquals("This is a changed test Entity", ((MyEntity) processInstance.getVariable("b")).getTest());
        assertEquals("This is a changed test SerializableObject", ((MyVariableSerializable) processInstance.getVariable("c")).getText());
        System.out.println("### Completing third work item ###");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        workItem = handler.getWorkItem();
        assertNull(workItem);
        
        result = emf.createEntityManager().createQuery("select i from VariableInstanceInfo i").getResultList();
        //This was 6.. but I change it to 0 because all the variables will go away with the process instance..
        //we need to change that to leave the variables there??? 
        assertEquals(0, result.size());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = (WorkflowProcessInstance)
			ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    public void testPersistenceVariablesWithTypeChange() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "VariablePersistenceStrategyProcessTypeChange.rf" ), ResourceType.DRF );
        for (KnowledgeBuilderError error: kbuilder.getErrors()) {
        	System.out.println(error);
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", new MyEntity("This is a test Entity with annotation in fields"));
        parameters.put("m", new MyEntityMethods("This is a test Entity with annotations in methods"));
        parameters.put("f", new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods"));
        parameters.put("z", new MyVariableSerializable("This is a test SerializableObject"));
        ProcessInstance processInstance = ksession.startProcess( "com.sample.ruleflow", parameters );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testPersistenceVariablesSubProcess() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "VariablePersistenceStrategySubProcess.rf" ), ResourceType.DRF );
        for (KnowledgeBuilderError error: kbuilder.getErrors()) {
        	System.out.println(error);
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", new MyEntity("This is a test Entity with annotation in fields"));
        parameters.put("m", new MyEntityMethods("This is a test Entity with annotations in methods"));
        parameters.put("f", new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods"));
        parameters.put("z", new MyVariableSerializable("This is a test SerializableObject"));
        ProcessInstance processInstance = ksession.startProcess( "com.sample.ruleflow", parameters );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testEntityWithSuperClassAnnotationField() throws Exception {
    	MySubEntity subEntity = new MySubEntity();
    	subEntity.setId(3L);
    	assertEquals(3L, JPAVariablePersister.getClassIdValue(subEntity));
    }
    
    public void testEntityWithSuperClassAnnotationMethod() throws Exception {
    	MySubEntityMethods subEntity = new MySubEntityMethods();
    	subEntity.setId(3L);
    	assertEquals(3L, JPAVariablePersister.getClassIdValue(subEntity));
    }
    
}