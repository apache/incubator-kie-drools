package org.drools.persistence.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistentStatefulSessionTest extends TestCase {

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

    }

    @Override
    protected void tearDown() throws Exception {
        ds1.close();
    }

    public void testLocalTransactionPerStatement() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List list = new ArrayList();

        ksession.setGlobal( "list",
                            list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

    public void testUserTransactions() throws Exception {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  $i : Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( $i );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        ut.commit();

        //      EntityManager em = emf.createEntityManager();
        //      SessionInfo sInfo = em.find( SessionInfo.class, 1 );
        //      assertNotNull( sInfo );
        //      //System.out.println( "session creation : " + sInfo.getVersion() );
        //      em.close();

        List list = new ArrayList();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 1 );
        ksession.insert( 2 );
//        ut.commit();
//
//        // insert and rollback
//        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
//        ut.begin();
        ksession.insert( 3 );
       // ut.rollback();

        // check we rolled back the state changes from the 3rd insert
        ksession.fireAllRules();
        ut.commit();
        System.out.println( list );
        assertEquals( 3,
                      list.size() );

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ksession.insert( 4 );
        ut.commit();

        // rollback again, this is testing that we can do consequetive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 5 );
        ksession.insert( 6 );
        ut.rollback();

        ksession.fireAllRules();

        assertEquals( 4,
                      list.size() );
        
        // now load the ksession
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( ksession.getId(), kbase, null, env );
        ut.commit();
        
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 7 );
        ksession.insert( 8 );
        ut.commit();

        ksession.fireAllRules();

        assertEquals( 6,
                      list.size() );
    }

    public void testPersistenceWorkItems() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertEquals( 1,
                      ksession.getObjects().size() );
        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }
        assertNull( processInstance );

    }
    
    public void testPersistenceWorkItems2() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );
        
        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ut.commit();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertEquals( 1,
                      ksession.getObjects().size() );
        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }
        assertNull( processInstance );

    }
    
    public void testPersistenceWorkItems3() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        ksession.getWorkItemManager().registerWorkItemHandler("MyWork", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testPersistenceState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "StateProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.insert(new ArrayList());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testPersistenceRuleSet() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "RuleSetProcess.rf" ),
                      ResourceType.DRF );
        kbuilder.add( new ClassPathResource( "RuleSetRules.drl" ),
                	  ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ksession.insert(new ArrayList());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.fireAllRules();
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testPersistenceEvents() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "EventsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession.signalEvent("MyEvent1", null, processInstance.getId());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession.signalEvent("MyEvent2", null, processInstance.getId());
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testProcessListener() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        final List<ProcessEvent> events = new ArrayList<ProcessEvent>();
        ProcessEventListener listener = new ProcessEventListener() {
			public void afterNodeLeft(ProcessNodeLeftEvent event) {
				System.out.println("After node left: " + event.getNodeInstance().getNodeName());
				events.add(event);				
			}
			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				System.out.println("After node triggered: " + event.getNodeInstance().getNodeName());
				events.add(event);				
			}
			public void afterProcessCompleted(ProcessCompletedEvent event) {
				System.out.println("After process completed");
				events.add(event);				
			}
			public void afterProcessStarted(ProcessStartedEvent event) {
				System.out.println("After process started");
				events.add(event);				
			}
			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
				System.out.println("Before node left: " + event.getNodeInstance().getNodeName());
				events.add(event);				
			}
			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
				System.out.println("Before node triggered: " + event.getNodeInstance().getNodeName());
				events.add(event);				
			}
			public void beforeProcessCompleted(ProcessCompletedEvent event) {
				System.out.println("Before process completed");
				events.add(event);				
			}
			public void beforeProcessStarted(ProcessStartedEvent event) {
				System.out.println("Before process started");
				events.add(event);				
			}
        };
        ksession.addEventListener(listener);
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );
        
        assertEquals(12, events.size());
        assertTrue(events.get(0) instanceof ProcessStartedEvent);
        assertTrue(events.get(1) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(2) instanceof ProcessNodeLeftEvent);
        assertTrue(events.get(3) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(4) instanceof ProcessNodeLeftEvent);
        assertTrue(events.get(5) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(6) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(7) instanceof ProcessNodeLeftEvent);
        assertTrue(events.get(8) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(9) instanceof ProcessNodeLeftEvent);
        assertTrue(events.get(10) instanceof ProcessNodeTriggeredEvent);
        assertTrue(events.get(11) instanceof ProcessStartedEvent);
        
        ksession.removeEventListener(listener);
        events.clear();
        
        processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );
        
        assertTrue(events.isEmpty());
    }

    public void testPersistenceSubProcess() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "SuperProcess.rf" ),
                      ResourceType.DRF );
        kbuilder.add( new ClassPathResource( "SubProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "com.sample.SuperProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    public void testPersistenceVariables() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "VariablesProcess.rf" ), ResourceType.DRF );
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
        parameters.put("name", "John Doe");
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess", parameters );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        assertEquals( "John Doe", workItem.getParameter("name"));

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        assertEquals( "John Doe", workItem.getParameter("text"));
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.getWorkItemManager().completeWorkItem( workItem.getId(), null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }

    public void testSetFocus() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "agenda-group \"badfocus\"";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List list = new ArrayList();

        ksession.setGlobal( "list",
                            list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();

        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

}
