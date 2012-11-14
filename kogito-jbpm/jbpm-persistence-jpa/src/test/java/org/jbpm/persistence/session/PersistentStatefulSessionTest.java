package org.jbpm.persistence.session;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;

import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.event.process.*;
import org.kie.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.*;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentStatefulSessionTest {

    private static Logger logger = LoggerFactory.getLogger(PersistentStatefulSessionTest.class);
    
    private HashMap<String, Object> context;
    private Environment env;

    @Rule
    public TestName testName = new TestName();
    
    @Before
    public void setUp() throws Exception {
        String methodName = testName.getMethodName();
        // Rules marshalling uses ObjectTypeNodes which screw up marshalling. 
        if( "testLocalTransactionPerStatement".equals(methodName) 
            || "testUserTransactions".equals(methodName) 
            || "testPersistenceRuleSet".equals(methodName)
            || "testSetFocus".equals(methodName)
            // Constraints in ruleflows are rules as well (I'm guessing?), so OTN's again.. 
            || "testPersistenceState".equals(methodName) ) { 
            context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        }
        else { 
            context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        }
        
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    private static String ruleString = ""
        + "package org.drools.test\n"
        + "global java.util.List list\n"
        + "rule rule1\n"
        + "when\n"
        + "  Integer(intValue > 0)\n"
        + "then\n"
        + "  list.add( 1 );\n"
        + "end\n"
        + "\n";
        
    @Test
    public void testLocalTransactionPerStatement() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( ruleString.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal( "list",
                            list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

    @Test
    public void testUserTransactions() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( ruleString.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        ut.commit();

        List<?> list = new ArrayList<Object>();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 1 );
        ksession.insert( 2 );
        ut.commit();

        // insert and rollback
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ut.rollback();

        // check we rolled back the state changes from the 3rd insert
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();        
        ksession.fireAllRules();
        ut.commit();
        assertEquals( 2,
                      list.size() );

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ksession.insert( 4 );
        ut.commit();

        // rollback again, this is testing that we can do consecutive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 5 );
        ksession.insert( 6 );
        ut.rollback();

        ksession.fireAllRules();

        assertEquals( 4,
                      list.size() );
        
        // now load the ksession
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( ksession.getId(), kbase, null, env );
        
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 7 );
        ksession.insert( 8 );
        ut.commit();

        ksession.fireAllRules();

        assertEquals( 6,
                      list.size() );
    }

    @Test
    public void testPersistenceWorkItems() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int origNumObjects = ksession.getObjects().size();
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        logger.debug( "Started process instance " + processInstance.getId() );

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
        assertEquals( origNumObjects + 1,
                      ksession.getObjects().size() );
        for ( Object o : ksession.getObjects() ) {
            logger.debug( o.toString() );
        }
        assertNull( processInstance );

    }
    
    @Test
    public void testPersistenceWorkItems2() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ), ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        logger.debug( "Started process instance " + processInstance.getId() );

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
            logger.debug( o.toString() );
        }
        assertNull( processInstance );

    }
    
    @Test
    public void testPersistenceWorkItems3() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        ksession.getWorkItemManager().registerWorkItemHandler("MyWork", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        ksession.insert( "TestString" );
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    @Test
    public void testPersistenceState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "StateProcess.rf" ), ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        logger.debug( "Started process instance " + processInstance.getId() );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        ksession.insert(new ArrayList<Object>());

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( id, kbase, null, env );
        processInstance = ksession.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }
    
    @Test
    public void testPersistenceRuleSet() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "RuleSetProcess.rf" ),
                      ResourceType.DRF );
        kbuilder.add( new ClassPathResource( "RuleSetRules.drl" ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ksession.insert(new ArrayList<Object>());

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
    
    @Test
    public void testPersistenceEvents() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "EventsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        logger.debug( "Started process instance " + processInstance.getId() );

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
    
    @Test
    public void testProcessListener() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        final List<ProcessEvent> events = new ArrayList<ProcessEvent>();
        ProcessEventListener listener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                logger.debug("After node left: " + event.getNodeInstance().getNodeName());
                events.add(event);              
            }
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.debug("After node triggered: " + event.getNodeInstance().getNodeName());
                events.add(event);              
            }
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                logger.debug("After process completed");
                events.add(event);              
            }
            public void afterProcessStarted(ProcessStartedEvent event) {
                logger.debug("After process started");
                events.add(event);              
            }
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                logger.debug("Before node left: " + event.getNodeInstance().getNodeName());
                events.add(event);              
            }
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.debug("Before node triggered: " + event.getNodeInstance().getNodeName());
                events.add(event);              
            }
            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                logger.debug("Before process completed");
                events.add(event);              
            }
            public void beforeProcessStarted(ProcessStartedEvent event) {
                logger.debug("Before process started");
                events.add(event);              
            }
            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                logger.debug("After Variable Changed");
                events.add(event);  
            }
            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                logger.debug("Before Variable Changed");
                events.add(event); 
            }
        };
        ksession.addEventListener(listener);
        
        ProcessInstance processInstance = ksession.startProcess( "org.drools.test.TestProcess" );
        logger.debug( "Started process instance " + processInstance.getId() );
        
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
        logger.debug( "Started process instance " + processInstance.getId() );
        
        assertTrue(events.isEmpty());
    }

    @Test
    public void testPersistenceSubProcess() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "SuperProcess.rf" ),
                      ResourceType.DRF );
        kbuilder.add( new ClassPathResource( "SubProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        int id = ksession.getId();
        
        ProcessInstance processInstance = ksession.startProcess( "com.sample.SuperProcess" );
        logger.debug( "Started process instance " + processInstance.getId() );

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
        assertNull( "Process did not complete.", processInstance );
    }
    
    @Test
    public void testPersistenceVariables() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "VariablesProcess.rf" ), ResourceType.DRF );
        for (KnowledgeBuilderError error: kbuilder.getErrors()) {
            logger.debug(error.toString());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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

    @Test
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

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

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
