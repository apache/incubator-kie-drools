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

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.task.Group;
import org.drools.task.User;
import org.drools.task.service.TaskService;
import org.drools.task.service.TaskServiceSession;
import org.drools.vsm.mina.MinaAcceptor;
import org.drools.vsm.mina.MinaConnector;
import org.drools.vsm.mina.MinaIoHandler;
import org.drools.vsm.remote.ServiceManagerRemoteClient;
import org.drools.vsm.task.TaskServerMessageHandlerImpl;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class ServiceManagerHumanTaskMinaRemoteTest extends ServiceManagerTestBase {

    AcceptorService server;
    AcceptorService humanTaskServer;
    private EntityManagerFactory emf;
    private TaskService taskService;
    protected TaskServiceSession taskSession;
    protected Map<String, User> users;
    protected Map<String, Group> groups;

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
}
