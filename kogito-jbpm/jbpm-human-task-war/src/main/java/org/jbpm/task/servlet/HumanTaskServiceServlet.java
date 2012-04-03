package org.jbpm.task.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.AccessType;
import org.jbpm.task.AllowedToDelegate;
import org.jbpm.task.Attachment;
import org.jbpm.task.BooleanExpression;
import org.jbpm.task.Comment;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.Delegation;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Notification;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.EscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.UserGroupCallback;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.jbpm.task.service.jms.JMSTaskServer;
import org.jbpm.task.service.jms.TaskServiceConstants;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class HumanTaskServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 5L;
	
	private TaskServer server = null;
    private Thread thread = null;

    /**
     * Responsible for configuring entire task server based in init parameters.
     * There are two sections of the configuration:
     * <ul>
     * 	<li>transport related - to configure transport of choice (hornetq, jms,mina)</li>
     * 	<li>general - configures internal components of task server (escalation, user group callback)</li>
     * </ul>
     * 
     * Main parameter that controls what trasport will be configured is <code>active.config</code>. It has three acceptable values:
     * <ul>
     * 	<li>hornetq</li>
     * 	<li>jms</li>
     * 	<li>mina</li>
     * </ul>
     * be default it uses hornetq as transport.
     * 
     * Dedicated parameters for transport configuration:
     * <b>HornetQ</b>
     * <ul>
     * 	<li>hornetq.port</li>
     * </ul>
     * <br/>
     * <b>JMS</b>
     * <ul>
     * 	<li>JMSTaskServer.connectionFactory</li>
     * 	<li>JMSTaskServer.transacted</li>
     * 	<li>JMSTaskServer.acknowledgeMode</li>
     * 	<li>JMSTaskServer.queueName</li>
     * 	<li>JMSTaskServer.responseQueueName</li>
     * </ul>
     * 
     * <b>Mina</b>
     * <ul>
	 *  <li>mina.host</li>
     * 	<li>mina.port</li>
     * </ul>
     */
	public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(getConfigParameter("task.persistence.unit", "org.jbpm.task"));
        
        String escalationHandlerClass = getConfigParameter("escalated.deadline.handler.class", DefaultEscalatedDeadlineHandler.class.getName());
        TaskService taskService = null;
        try {
        	EscalatedDeadlineHandler handler = getInstance(escalationHandlerClass);
        	if (handler instanceof DefaultEscalatedDeadlineHandler) {
        		UserInfo userInfo = null;
        		try {
	        		String userInfoClass = getConfigParameter("user.info.class", null);
		        	userInfo = getInstance(userInfoClass);
        		} catch (IllegalArgumentException e) {
        			Properties registryProps = new Properties();
        			registryProps.load(this.getClass().getResourceAsStream("/userinfo.properties"));
//					userInfo = new DefaultUserInfo(registryProps);
				}
	        	
	        	((DefaultEscalatedDeadlineHandler)handler).setUserInfo(userInfo);
        	}
        	
        	taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener(), handler);
        } catch (Exception e) {
        	taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		}
        
        TaskServiceSession taskSession = taskService.createSession();
        
        String usersConfig = getConfigParameter("load.users", "");
        String groupsConfig = getConfigParameter("load.groups", "");
        try {
            if (usersConfig != null && usersConfig.length() > 0) {
                if (usersConfig.endsWith(".mvel")) {
                   
                    Map vars = new HashMap();
                    Reader reader = new InputStreamReader(getConfigFileStream(usersConfig) );     
                    Map<String, User> users = ( Map<String, User> ) eval( reader, vars );   
                    for ( User user : users.values() ) {
                        taskSession.addUser( user );
                    }  
                } else if (usersConfig.endsWith(".properties"))  {
                    Properties props = new Properties();
                    props.load(getConfigFileStream(usersConfig));
                    
                    Set<String> ids = props.stringPropertyNames();
                    for (String id : ids) {
                        taskSession.addUser( new User(id) );
                    }
                }
            }
    	} catch (Exception e) {
            System.err.println("Problem loading users from specified file: " + usersConfig + " error message: " + e);
        }
        try {
            if (groupsConfig != null && groupsConfig.length() > 0) {
                if (groupsConfig.endsWith(".mvel")) {
                    Map vars = new HashMap();
                    Reader reader = new InputStreamReader( getConfigFileStream(groupsConfig) );      
                    Map<String, Group> groups = ( Map<String, Group> ) eval( reader, vars );     
                    for ( Group group : groups.values() ) {
                        taskSession.addGroup( group );
                    }
                } else if (groupsConfig.endsWith(".properties"))  {
                    Properties props = new Properties();
                    props.load(getConfigFileStream(groupsConfig));
                    
                    Set<String> ids = props.stringPropertyNames();
                    for (String id : ids) {
                        taskSession.addGroup( new Group(id) );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Problem loading groups from specified file: " + groupsConfig + " error message: " + e);
        }
                 
        
        String activeConfig = getConfigParameter("active.config", "hornetq");
        
        
        if ("mina".equalsIgnoreCase(activeConfig)) {
	        int port = Integer.parseInt(getConfigParameter("mina.port", "9123"));
        	String host = getConfigParameter("mina.host", "localhost");
        	// start server
	        server = new MinaTaskServer(taskService, port, host);
	        thread = new Thread(server);
	        thread.start();
	        System.out.println("Apache Mina Task service started correctly !");
	        System.out.println("Apache Mina Task service running ...");
	        
        } else if ("hornetq".equalsIgnoreCase(activeConfig)) {
        	int port = Integer.parseInt(getConfigParameter("hornetq.port", "5446"));
        	
        	server = new HornetQTaskServer(taskService, port);
    		thread = new Thread(server);
    		thread.start();
    		System.out.println("HornetQ Task service started correctly !");
	        System.out.println("HornetQ Task service running ...");
	        
        } else if ("jms".equalsIgnoreCase(activeConfig)) {
        	Properties connProperties = new Properties();
        	connProperties.setProperty(TaskServiceConstants.TASK_SERVER_CONNECTION_FACTORY_NAME, getConfigParameter(TaskServiceConstants.TASK_SERVER_CONNECTION_FACTORY_NAME, null));
        	connProperties.setProperty(TaskServiceConstants.TASK_SERVER_TRANSACTED_NAME, getConfigParameter(TaskServiceConstants.TASK_SERVER_TRANSACTED_NAME, null));
        	connProperties.setProperty(TaskServiceConstants.TASK_SERVER_ACKNOWLEDGE_MODE_NAME, getConfigParameter(TaskServiceConstants.TASK_SERVER_ACKNOWLEDGE_MODE_NAME, ""));
        	connProperties.setProperty(TaskServiceConstants.TASK_SERVER_QUEUE_NAME_NAME, getConfigParameter(TaskServiceConstants.TASK_SERVER_QUEUE_NAME_NAME, null));
        	connProperties.setProperty(TaskServiceConstants.TASK_SERVER_RESPONSE_QUEUE_NAME_NAME, getConfigParameter(TaskServiceConstants.TASK_SERVER_RESPONSE_QUEUE_NAME_NAME, null));
        	try {
	        	server = new JMSTaskServer(taskService, connProperties, new InitialContext());
	        	thread = new Thread(server);
	    		thread.start();
	    		System.out.println("JMS Task service started correctly !");
		        System.out.println("JMS Task service running ...");
			} catch (NamingException e) {
				throw new ServletException("Error while starting JMS Task Service", e);
			}
        }
        
        UserGroupCallbackManager manager = UserGroupCallbackManager.getInstance();
        
        if (!manager.existsCallback()) {
            String callbackClass = getConfigParameter("user.group.callback.class", "");
            
            
        	UserGroupCallback userGroupCallback = getInstance(callbackClass);
        		
        	manager.setCallback(userGroupCallback);
        }
        taskSession.dispose();
        System.out.println("Task service startup completed successfully !");
        
    }
	
	

    @Override
	public void destroy() {

		try {
			this.server.stop();
		} catch (Exception e) {
			System.out.println("Exception while stopping task server " + e.getMessage());
		}
		try {
			 this.thread.interrupt();
		} catch (Exception e) {
			System.out.println("Exception while stopping task server thread " + e.getMessage());
		}
	}



	protected TaskServer getServer() {
		return server;
	}



	public static Object eval(Reader reader, Map vars) {
        try {
            return eval( readerToString( reader ), vars );
        } catch ( IOException e ) {
            throw new RuntimeException( "Exception Thrown", e );
        }
    }
    
    public static String readerToString(Reader reader) throws IOException {
        int charValue = 0;
        StringBuffer sb = new StringBuffer( 1024 );
        while ( (charValue = reader.read()) != -1 ) {
            //result = result + (char) charValue;
            sb.append( (char) charValue );
        }
        return sb.toString();
    }

    public static Object eval(String str, Map vars) {
        ExpressionCompiler compiler = new ExpressionCompiler( str.trim() );

        ParserContext context = new ParserContext();
        context.addPackageImport( "org.jbpm.task" );
        context.addPackageImport( "java.util" );
        
        context.addImport( "AccessType", AccessType.class );
        context.addImport( "AllowedToDelegate", AllowedToDelegate.class );
        context.addImport( "Attachment", Attachment.class );
        context.addImport( "BooleanExpression", BooleanExpression.class );
        context.addImport( "Comment", Comment.class );
        context.addImport( "Deadline", Deadline.class );
        context.addImport( "Deadlines", Deadlines.class );
        context.addImport( "Delegation", Delegation.class );
        context.addImport( "Escalation", Escalation.class );
        context.addImport( "Group", Group.class );
        context.addImport( "I18NText", I18NText.class );
        context.addImport( "Notification", Notification.class );
        context.addImport( "OrganizationalEntity", OrganizationalEntity.class );
        context.addImport( "PeopleAssignments", PeopleAssignments.class );
        context.addImport( "Reassignment", Reassignment.class );
        context.addImport( "Status", Status.class );
        context.addImport( "Task", Task.class );
        context.addImport( "TaskData", TaskData.class );
        context.addImport( "TaskSummary", TaskSummary.class );
        context.addImport( "User", User.class );

        return MVEL.executeExpression( compiler.compile( context ), vars );
    }
    
    protected String getConfigParameter(String name, String defaultValue) {
    	String paramValue = getInitParameter(name);
    	
    	if (paramValue != null && paramValue.length() > 0) {
    		return paramValue;
    	}
    	if (defaultValue == null) {
    		throw new IllegalArgumentException("Missing configuration property name: " + name);
    	}
    	return defaultValue;
    }
    
    protected <T> T getInstance(String className) {
        if (className == null || "".equalsIgnoreCase(className)) {
            
            return null;
        }
        
        Object instance;
		try {
			instance = Class.forName(className).newInstance();
		
			return (T) instance;
		} catch (Exception e) {
			throw new RuntimeException("Error while creating instance of configurable class, class name: " + className, e);
		}
    }
    
    protected InputStream getConfigFileStream(String location) throws IOException {
        if (location == null) {
            return null;
        }
        
        URL configLocation = null;

        if (location.startsWith("classpath:")) {
            String pathOnly = location.replaceFirst("classpath:", "");
            configLocation = HumanTaskServiceServlet.class.getResource(pathOnly);
        } else {
            configLocation = new URL(location);
        }
                
        return configLocation.openStream();
    }

    protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// response.sendError(1001, "GET Method Not Allowed Here");
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest reqquest,
			HttpServletResponse response) throws ServletException, IOException {
		response.sendError(1001, "POST Method Not Allowed Here");
	}
}
