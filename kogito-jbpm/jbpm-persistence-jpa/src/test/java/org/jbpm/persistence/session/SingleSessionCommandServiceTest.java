package org.jbpm.persistence.session;

import static org.drools.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.log4j.xml.DOMConfigurator;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.GetProcessInstanceCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.jpa.JpaJDKTimerService;
import org.drools.persistence.jpa.JpaTimeJobFactoryManager;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManagerFactory;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.rule.Package;
import org.drools.runtime.Environment;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.jbpm.compiler.ProcessBuilderImpl;
import org.jbpm.persistence.JbpmTestCase;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class SingleSessionCommandServiceTest extends JbpmTestCase {

	private HashMap<String, Object> context;
	private Environment env;
    
    static {
		DOMConfigurator.configure(SingleSessionCommandServiceTest.class.getResource("/log4j.xml"));
    }
    
    public void setUp() {
        String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        if( testMethodName.startsWith("testPersistenceTimer") ) { 
            context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        }
        else { 
            context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        }
        env = PersistenceUtil.createEnvironment(context);
    }

    @After
    public void tearDown() {
        PersistenceUtil.tearDown(context);
    }

    @AfterClass
    public static void compareMarshallingData() throws Exception {
       MarshallingTestUtil.compareMarshallingDataFromTest(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @Test
    public void testPersistenceWorkItems() throws Exception {
        setUp();
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessWorkItems();
        kbase.addKnowledgePackages( kpkgs );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                SingleSessionCommandService.class.getName() );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                JPAProcessInstanceManagerFactory.class.getName() );
        properties.setProperty( "drools.workItemManagerFactory",
                                JPAWorkItemManagerFactory.class.getName() );
        properties.setProperty( "drools.processSignalManagerFactory",
                                JPASignalManagerFactory.class.getName() );
        properties.setProperty( "drools.timerService",
                                JpaJDKTimerService.class.getName() );
        SessionConfiguration config = new SessionConfiguration( properties );

        SingleSessionCommandService service = new SingleSessionCommandService( kbase,
                                                                               config,
                                                                               env );
        int sessionId = service.getSessionId();

        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessId( "org.drools.test.TestProcess" );
        ProcessInstance processInstance = service.execute( startProcessCommand );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        CompleteWorkItemCommand completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );

        workItem = handler.getWorkItem();
        assertNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNull( processInstance );
        service.dispose();
    }
    
    @Test
    public void testPersistenceWorkItemsUserTransaction() throws Exception {
        setUp();
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessWorkItems();
        kbase.addKnowledgePackages( kpkgs );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                SingleSessionCommandService.class.getName() );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                JPAProcessInstanceManagerFactory.class.getName() );
        properties.setProperty( "drools.workItemManagerFactory",
                                JPAWorkItemManagerFactory.class.getName() );
        properties.setProperty( "drools.processSignalManagerFactory",
                                JPASignalManagerFactory.class.getName() );
        properties.setProperty( "drools.timerService",
                                JpaJDKTimerService.class.getName() );
        SessionConfiguration config = new SessionConfiguration( properties );

        SingleSessionCommandService service = new SingleSessionCommandService( kbase,
                                                                               config,
                                                                               env );
        int sessionId = service.getSessionId();

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessId( "org.drools.test.TestProcess" );
        ProcessInstance processInstance = service.execute( startProcessCommand );
        System.out.println( "Started process instance " + processInstance.getId() );
        ut.commit();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );
        ut.commit();
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        CompleteWorkItemCommand completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );
        ut.commit();

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        ut.commit();
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );
        ut.commit();

        workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        ut.commit();
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );
        ut.commit();

        workItem = handler.getWorkItem();
        assertNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        ut.begin();
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        ut.commit();
        assertNull( processInstance );
        service.dispose();
    }

	private Collection<KnowledgePackage> getProcessWorkItems() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( "org.drools.test.TestProcess" );
        process.setName( "TestProcess" );
        process.setPackageName( "org.drools.test" );
        StartNode start = new StartNode();
        start.setId( 1 );
        start.setName( "Start" );
        process.addNode( start );
        ActionNode actionNode = new ActionNode();
        actionNode.setId( 2 );
        actionNode.setName( "Action" );
        DroolsConsequenceAction action = new DroolsConsequenceAction();
        action.setDialect( "java" );
        action.setConsequence( "System.out.println(\"Executed action\");" );
        actionNode.setAction( action );
        process.addNode( actionNode );
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            actionNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setId( 3 );
        workItemNode.setName( "WorkItem1" );
        Work work = new WorkImpl();
        work.setName( "MyWork" );
        workItemNode.setWork( work );
        process.addNode( workItemNode );
        new ConnectionImpl( actionNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            workItemNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        WorkItemNode workItemNode2 = new WorkItemNode();
        workItemNode2.setId( 4 );
        workItemNode2.setName( "WorkItem2" );
        work = new WorkImpl();
        work.setName( "MyWork" );
        workItemNode2.setWork( work );
        process.addNode( workItemNode2 );
        new ConnectionImpl( workItemNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            workItemNode2,
                            Node.CONNECTION_DEFAULT_TYPE );
        WorkItemNode workItemNode3 = new WorkItemNode();
        workItemNode3.setId( 5 );
        workItemNode3.setName( "WorkItem3" );
        work = new WorkImpl();
        work.setName( "MyWork" );
        workItemNode3.setWork( work );
        process.addNode( workItemNode3 );
        new ConnectionImpl( workItemNode2,
                            Node.CONNECTION_DEFAULT_TYPE,
                            workItemNode3,
                            Node.CONNECTION_DEFAULT_TYPE );
        EndNode end = new EndNode();
        end.setId( 6 );
        end.setName( "End" );
        process.addNode( end );
        new ConnectionImpl( workItemNode3,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        PackageBuilder packageBuilder = new PackageBuilder();
        ProcessBuilderImpl processBuilder = new ProcessBuilderImpl( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

    @Test
    public void testPersistenceSubProcess() {
        setUp();
        
        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                SingleSessionCommandService.class.getName() );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                JPAProcessInstanceManagerFactory.class.getName() );
        properties.setProperty( "drools.workItemManagerFactory",
                                JPAWorkItemManagerFactory.class.getName() );
        properties.setProperty( "drools.processSignalManagerFactory",
                                JPASignalManagerFactory.class.getName() );
        properties.setProperty( "drools.timerService",
                                JpaJDKTimerService.class.getName() );
        SessionConfiguration config = new SessionConfiguration( properties );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package pkg = getProcessSubProcess();
        ruleBase.addPackage( pkg );

        SingleSessionCommandService service = new SingleSessionCommandService( ruleBase,
                                                                               config,
                                                                               env );
        int sessionId = service.getSessionId();
        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessId( "org.drools.test.TestProcess" );
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) service.execute( startProcessCommand );
        System.out.println( "Started process instance " + processInstance.getId() );
        long processInstanceId = processInstance.getId();

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
        		                                   ruleBase,
                                                   config,
                                                   env );
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstanceId );
        processInstance = (RuleFlowProcessInstance) service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );

        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertEquals( 1,
                      nodeInstances.size() );
        SubProcessNodeInstance subProcessNodeInstance = (SubProcessNodeInstance) nodeInstances.iterator().next();
        long subProcessInstanceId = subProcessNodeInstance.getProcessInstanceId();
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( subProcessInstanceId );
        RuleFlowProcessInstance subProcessInstance = (RuleFlowProcessInstance) service.execute( getProcessInstanceCommand );
        assertNotNull( subProcessInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   ruleBase,
                                                   config,
                                                   env );
        CompleteWorkItemCommand completeWorkItemCommand = new CompleteWorkItemCommand();
        completeWorkItemCommand.setWorkItemId( workItem.getId() );
        service.execute( completeWorkItemCommand );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   ruleBase,
                                                   config,
                                                   env );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( subProcessInstanceId );
        subProcessInstance = (RuleFlowProcessInstance) service.execute( getProcessInstanceCommand );
        assertNull( subProcessInstance );

        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstanceId );
        processInstance = (RuleFlowProcessInstance) service.execute( getProcessInstanceCommand );
        assertNull( processInstance );
        service.dispose();
    }

	private Package getProcessSubProcess() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( "org.drools.test.TestProcess" );
        process.setName( "TestProcess" );
        process.setPackageName( "org.drools.test" );
        
        StartNode start = new StartNode();
        start.setId( 1 );
        start.setName( "Start" );
        process.addNode( start );
        
        ActionNode actionNode = new ActionNode();
        actionNode.setId( 2 );
        actionNode.setName( "Action" );
        
        DroolsConsequenceAction action = new DroolsConsequenceAction();
        action.setDialect( "java" );
        action.setConsequence( "System.out.println(\"Executed action\");" );
        actionNode.setAction( action );
        process.addNode( actionNode );
        
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            actionNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setId( 3 );
        subProcessNode.setName( "SubProcess" );
        subProcessNode.setProcessId( "org.drools.test.SubProcess" );
        process.addNode( subProcessNode );
        
        new ConnectionImpl( actionNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            subProcessNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        
        EndNode end = new EndNode();
        end.setId( 4 );
        end.setName( "End" );
        process.addNode( end );
        
        new ConnectionImpl( subProcessNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        PackageBuilder packageBuilder = new PackageBuilder();
        ProcessBuilderImpl processBuilder = new ProcessBuilderImpl( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );

        process = new RuleFlowProcess();
        process.setId( "org.drools.test.SubProcess" );
        process.setName( "SubProcess" );
        process.setPackageName( "org.drools.test" );
        
        start = new StartNode();
        start.setId( 1 );
        start.setName( "Start" );
        process.addNode( start );
        
        actionNode = new ActionNode();
        actionNode.setId( 2 );
        actionNode.setName( "Action" );
        
        action = new DroolsConsequenceAction();
        action.setDialect( "java" );
        action.setConsequence( "System.out.println(\"Executed action\");" );
        actionNode.setAction( action );
        process.addNode( actionNode );
        
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            actionNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setId( 3 );
        workItemNode.setName( "WorkItem1" );
        
        Work work = new WorkImpl();
        work.setName( "MyWork" );
        workItemNode.setWork( work );
        process.addNode( workItemNode );
        
        new ConnectionImpl( actionNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            workItemNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        
        end = new EndNode();
        end.setId( 6 );
        end.setName( "End" );
        process.addNode( end );
        
        new ConnectionImpl( workItemNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        processBuilder.buildProcess( process,
                                     null );
        return packageBuilder.getPackage();
    }

    @Test
    public void testPersistenceTimer() throws Exception {
        setUp();
        
        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                SingleSessionCommandService.class.getName() );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                JPAProcessInstanceManagerFactory.class.getName() );
        properties.setProperty( "drools.workItemManagerFactory",
                                JPAWorkItemManagerFactory.class.getName() );
        properties.setProperty( "drools.processSignalManagerFactory",
                                JPASignalManagerFactory.class.getName() );
        
        SessionConfiguration config = new SessionConfiguration( properties );
        config.setTimerJobFactoryManager( new JpaTimeJobFactoryManager( ) );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessTimer();
        kbase.addKnowledgePackages( kpkgs );

        SingleSessionCommandService service = new SingleSessionCommandService( kbase,
                                                                               config,
                                                                               env );
        int sessionId = service.getSessionId();
        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessId( "org.drools.test.TestProcess" );
        ProcessInstance processInstance = service.execute( startProcessCommand );
        System.out.println( "Started process instance " + processInstance.getId() );
        
        
        Thread.sleep( 500 );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNotNull( processInstance );
        service.dispose();

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        Thread.sleep( 5000 );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNull( processInstance );
    }

	private List<KnowledgePackage> getProcessTimer() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( "org.drools.test.TestProcess" );
        process.setName( "TestProcess" );
        process.setPackageName( "org.drools.test" );
        StartNode start = new StartNode();
        start.setId( 1 );
        start.setName( "Start" );
        process.addNode( start );
        TimerNode timerNode = new TimerNode();
        timerNode.setId( 2 );
        timerNode.setName( "Timer" );
        Timer timer = new Timer();
        timer.setDelay( "2000" );
        timerNode.setTimer( timer );
        process.addNode( timerNode );
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            timerNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        ActionNode actionNode = new ActionNode();
        actionNode.setId( 3 );
        actionNode.setName( "Action" );
        DroolsConsequenceAction action = new DroolsConsequenceAction();
        action.setDialect( "java" );
        action.setConsequence( "System.out.println(\"Executed action\");" );
        actionNode.setAction( action );
        process.addNode( actionNode );
        new ConnectionImpl( timerNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            actionNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        EndNode end = new EndNode();
        end.setId( 6 );
        end.setName( "End" );
        process.addNode( end );
        new ConnectionImpl( actionNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        PackageBuilder packageBuilder = new PackageBuilder();
        ProcessBuilderImpl processBuilder = new ProcessBuilderImpl( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

    @Test
    public void testPersistenceTimer2() throws Exception {
        setUp();
        
        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                SingleSessionCommandService.class.getName() );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                JPAProcessInstanceManagerFactory.class.getName() );
        properties.setProperty( "drools.workItemManagerFactory",
                                JPAWorkItemManagerFactory.class.getName() );
        properties.setProperty( "drools.processSignalManagerFactory",
                                JPASignalManagerFactory.class.getName() );

        SessionConfiguration config = new SessionConfiguration( properties );
        config.setTimerJobFactoryManager( new JpaTimeJobFactoryManager( ) );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessTimer2();
        kbase.addKnowledgePackages( kpkgs );

        SingleSessionCommandService service = new SingleSessionCommandService( kbase,
                                                                               config,
                                                                               env );
        int sessionId = service.getSessionId();
        StartProcessCommand startProcessCommand = new StartProcessCommand();
        startProcessCommand.setProcessId( "org.drools.test.TestProcess" );
        ProcessInstance processInstance = service.execute( startProcessCommand );
        System.out.println( "Started process instance " + processInstance.getId() );

        Thread.sleep( 2000 );

        service = new SingleSessionCommandService( sessionId,
                                                   kbase,
                                                   config,
                                                   env );
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNull( processInstance );
    }

	private List<KnowledgePackage> getProcessTimer2() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( "org.drools.test.TestProcess" );
        process.setName( "TestProcess" );
        process.setPackageName( "org.drools.test" );
        StartNode start = new StartNode();
        start.setId( 1 );
        start.setName( "Start" );
        process.addNode( start );
        TimerNode timerNode = new TimerNode();
        timerNode.setId( 2 );
        timerNode.setName( "Timer" );
        Timer timer = new Timer();
        timer.setDelay( "0" );
        timerNode.setTimer( timer );
        process.addNode( timerNode );
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            timerNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        ActionNode actionNode = new ActionNode();
        actionNode.setId( 3 );
        actionNode.setName( "Action" );
        DroolsConsequenceAction action = new DroolsConsequenceAction();
        action.setDialect( "java" );
        action.setConsequence( "try { Thread.sleep(1000); } catch (Throwable t) {} System.out.println(\"Executed action\");" );
        actionNode.setAction( action );
        process.addNode( actionNode );
        new ConnectionImpl( timerNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            actionNode,
                            Node.CONNECTION_DEFAULT_TYPE );
        EndNode end = new EndNode();
        end.setId( 6 );
        end.setName( "End" );
        process.addNode( end );
        new ConnectionImpl( actionNode,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        PackageBuilder packageBuilder = new PackageBuilder();
        ProcessBuilderImpl processBuilder = new ProcessBuilderImpl( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

}
