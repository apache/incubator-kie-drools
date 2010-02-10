package org.drools.vsm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.TestCase;

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.task.Group;
import org.drools.task.User;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.ContentData;
import org.drools.task.service.HumanTaskServiceImpl;
import org.drools.task.service.TaskService;
import org.drools.task.service.TaskServiceSession;
import org.drools.vsm.mina.MinaAcceptor;
import org.drools.vsm.mina.MinaConnector;
import org.drools.vsm.mina.MinaIoHandler;
import org.drools.vsm.remote.ServiceManagerRemoteClient;
import org.drools.vsm.remote.StatefulKnowledgeSessionRemoteClient;
import org.drools.vsm.task.TaskServerMessageHandlerImpl;
import org.drools.vsm.task.responseHandlers.BlockingTaskOperationMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingTaskSummaryMessageResponseHandler;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class ServiceManagerHumanTaskMinaRemoteTest extends TestCase {
    //extends ServiceManagerTestBase {

    AcceptorService server;
    AcceptorService humanTaskServer;
    private EntityManagerFactory emf;
    private TaskService taskService;
    protected TaskServiceSession taskSession;
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    private HumanTaskService htClient;
    private static final int DEFAULT_WAIT_TIME = 5000;
    protected ServiceManager client;
    
    protected void setUp() throws Exception {
        // Configure persistence to be used inside the WSHT Service
        // Use persistence.xml configuration
        emf = Persistence.createEntityManagerFactory("org.drools.task");
        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();
        
        Map<String, Object> vars = new HashedMap();
        Reader reader = null;

        try {
            reader = new InputStreamReader(ServiceManagerTestBase.class.getResourceAsStream("/rules/LoadUsers.mvel"));
            users = (Map<String, User>) eval(reader, vars);
            for (User user : users.values())
                taskSession.addUser(user);
        } finally {
            if (reader != null) reader.close();
            reader = null;
        }

        try {
            reader = new InputStreamReader(ServiceManagerTestBase.class.getResourceAsStream("/rules/LoadGroups.mvel"));
            groups = (Map<String, Group>) eval(reader,  vars);
            for (Group group : groups.values())
                taskSession.addGroup(group);
        } finally {
            if (reader != null) reader.close();
        }

        // END

        // Execution Server
        SocketAddress address = new InetSocketAddress("127.0.0.1",
                9123);
        ServiceManagerData serverData = new ServiceManagerData();
        // setup Server
        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener(),
                new GenericMessageHandlerImpl(serverData,
                SystemEventListenerFactory.getSystemEventListener())));
        this.server = new MinaAcceptor(acceptor,
                address);
        this.server.start();
        // End Execution Server
       

        // Human task Server configuration
        SocketAddress htAddress = new InetSocketAddress("127.0.0.1",
                9124);
        SocketAcceptor htAcceptor = new NioSocketAcceptor();

        htAcceptor.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener(),
                new TaskServerMessageHandlerImpl(taskService,
                SystemEventListenerFactory.getSystemEventListener())));
        this.humanTaskServer = new MinaAcceptor(htAcceptor, htAddress);
        this.humanTaskServer.start();


        // End Human task Server configuration

        // setup the ht client
        NioSocketConnector htclientConnector = new NioSocketConnector();
        htclientConnector.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener()));
        GenericConnector htMinaClient = new MinaConnector( "client ht",
                                                         htclientConnector,
                                                         htAddress,
                                                         SystemEventListenerFactory.getSystemEventListener() );

         // setup the SM client
        NioSocketConnector clientConnector = new NioSocketConnector();
        clientConnector.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener()));
        GenericConnector minaClient = new MinaConnector("client SM",
                clientConnector,
                address,
                SystemEventListenerFactory.getSystemEventListener());

        // Service Manager client, that contains a list of service beside the execution Server, in this case the HumanTaskService
        List<GenericConnector> services = new ArrayList<GenericConnector>();
        services.add(htMinaClient);
        this.client = new ServiceManagerRemoteClient("client SM",
                minaClient
                //List of service
                , services);

        ((ServiceManagerRemoteClient) client).connect();

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ((ServiceManagerRemoteClient) client).disconnect();
        this.server.stop();
        this.humanTaskServer.stop();

    }
    
    public Object eval(Reader reader, Map<String, Object> vars) {
    	try {
    		return eval(toString(reader), vars);
    	} catch (IOException e) {
    		throw new RuntimeException("Exception Thrown", e);
    	}
    }
    
    public Object eval(String str, Map<String, Object> vars) {
        ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

        ParserContext context = new ParserContext();
        context.addPackageImport("org.drools.task");
        context.addPackageImport("org.drools.task.service");
        context.addPackageImport("org.drools.task.query");
        context.addPackageImport("java.util");

        vars.put("now", new Date());
        return MVEL.executeExpression(compiler.compile(context), vars);
    }
    
    public String toString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(1024);
        int charValue;
        while ((charValue = reader.read()) != -1)
            sb.append((char) charValue);
        return sb.toString();
    }
    
    public void testEmpty() {
    }
    
     public void TODOtestHumanTasks() throws Exception {

    	KnowledgeBuilderFactoryService kbuilderFactory = this.client.getKnowledgeBuilderFactoryService();
    	KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
    	kbuilder.add( new ClassPathResource("rules/humanTasks.rf"),
    			ResourceType.DRF );

    	if ( kbuilder.hasErrors() ) {
    		System.out.println( "Errors: " + kbuilder.getErrors() );
    	}

    	KnowledgeBaseFactoryService kbaseFactory = this.client.getKnowledgeBaseFactoryService();
    	KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();

    	kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

    	StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

    	((StatefulKnowledgeSessionRemoteClient)ksession).registerWorkItemHandler("Human Task", "org.drools.vsm.task.CommandBasedVSMWSHumanTaskHandler");
    	ProcessInstance processInstance = ksession.startProcess("org.drools.test.humanTasks");
    	HumanTaskServiceProvider humanTaskServiceFactory = this.client.getHumanTaskService();
    	htClient = humanTaskServiceFactory.newHumanTaskServiceClient();

    	Thread.sleep(1000);

    	ksession.fireAllRules();

    	System.out.println("First Task Execution");
    	assertEquals(true , executeNextTask("lucaz"));
    	Thread.sleep(8000);
    	System.out.println("Second Task Execution");
    	assertEquals(true , executeNextTask("lucaz"));
    	System.out.println("Inexistent Task Execution");
    	Thread.sleep(8000);
    	assertEquals(false, executeNextTask("lucaz"));

    }

    private boolean executeNextTask(String user) {

    	BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).getTasksAssignedAsPotentialOwner(user, "en-UK", responseHandler);
    	responseHandler.waitTillDone(DEFAULT_WAIT_TIME);

    	if (responseHandler.getResults().size()==0)
    		return false;

    	TaskSummary task = responseHandler.getResults().get(0);
    	ContentData data = new ContentData();
    	data.setContent("next step".getBytes());

    	BlockingTaskOperationMessageResponseHandler startResponseHandler = new BlockingTaskOperationMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).start(task.getId(), user, startResponseHandler );
    	startResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);

    	System.out.println("Started Task " + task.getId());

    	BlockingTaskOperationMessageResponseHandler completeResponseHandler = new BlockingTaskOperationMessageResponseHandler();
    	((HumanTaskServiceImpl)htClient).complete(task.getId(), user, null , completeResponseHandler);
    	completeResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
    	System.out.println("Completed Task " + task.getId());

    	return true;
    }

}
