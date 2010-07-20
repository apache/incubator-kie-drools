package org.drools.persistence.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

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
import org.drools.compiler.ProcessBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.core.timer.Timer;
import org.drools.rule.Package;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.instance.node.SubProcessNodeInstance;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SingleSessionCommandServiceTest extends TestCase {

	private PoolingDataSource ds1;
	private EntityManagerFactory emf;
	private static Server h2Server;
    
    static {
    	try {
			DeleteDbFiles.execute("", "JPADroolsFlow", true);
			h2Server = Server.createTcpServer(new String[0]);
			h2Server.start();
		} catch (SQLException e) {
			throw new RuntimeException("can't start h2 server db",e);
		}
		DOMConfigurator.configure(SingleSessionCommandServiceTest.class.getResource("/log4j.xml"));
    }
    
    @Override
    protected void finalize() throws Throwable {
    	if (h2Server != null) {
    		h2Server.stop();
    	}
    	DeleteDbFiles.execute("", "JPADroolsFlow", true);
    	super.finalize();
    }
    
    protected void setUp() {
    	
        ds1 = new PoolingDataSource();
        ds1.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
    	ds1.setUniqueName("jdbc/testDS1");
    	ds1.setMaxPoolSize(5);
    	ds1.setAllowLocalTransactions(true);
    	ds1.getDriverProperties().setProperty("driverClassName", "org.h2.Driver");
    	ds1.getDriverProperties().setProperty("url", "jdbc:h2:tcp://localhost/JPADroolsFlow");
    	ds1.getDriverProperties().setProperty("user", "sa");
    	ds1.getDriverProperties().setProperty("password", "");
        
        
//        ds1.setUniqueName( "jdbc/testDS1" );
//        ds1.setClassName( "org.h2.Driver" );
//        ds1.setMaxPoolSize( 3 );
//        ds1.setAllowLocalTransactions( true );
//        ds1.getDriverProperties().put( "user",
//                                       "sa" );
//        ds1.getDriverProperties().put( "password",
//                                       "" );
//        ds1.getDriverProperties().put( "URL",
//                                       "jdbc:h2:tcp://localhost/JPADroolsFlow" );
        ds1.init();

        emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
    }

    protected void tearDown() {
        emf.close();
        ds1.close();
    }

    public void testPersistenceWorkItems() throws Exception {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessWorkItems();
        kbase.addKnowledgePackages( kpkgs );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
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

    public void testPersistenceWorkItemsUserTransaction() throws Exception {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> kpkgs = getProcessWorkItems();
        kbase.addKnowledgePackages( kpkgs );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
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

    @SuppressWarnings("unused")
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
        ProcessBuilder processBuilder = new ProcessBuilder( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

    public void testPersistenceSubProcess() {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
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

    @SuppressWarnings("unused")
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
        ProcessBuilder processBuilder = new ProcessBuilder( packageBuilder );
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

    public void testPersistenceTimer() throws Exception {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        SessionConfiguration config = new SessionConfiguration( properties );

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
        Thread.sleep( 3000 );
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId( processInstance.getId() );
        processInstance = service.execute( getProcessInstanceCommand );
        assertNull( processInstance );
    }

    @SuppressWarnings("unused")
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
        ProcessBuilder processBuilder = new ProcessBuilder( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

    public void testPersistenceTimer2() throws Exception {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        SessionConfiguration config = new SessionConfiguration( properties );

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

    @SuppressWarnings("unused")
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
        ProcessBuilder processBuilder = new ProcessBuilder( packageBuilder );
        processBuilder.buildProcess( process,
                                     null );
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>();
        list.add( new KnowledgePackageImp( packageBuilder.getPackage() ) );
        return list;
    }

}
