package org.jbpm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.mina.MinaTaskServer;

public class DemoTaskService {
	
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        TaskServiceSession taskSession = taskService.createSession();
        // Add users
        Map vars = new HashMap();
        InputStream usersin = DemoTaskService.class.getResourceAsStream( "LoadUsers.mvel" );
        if(usersin != null) {
        	Reader reader = new InputStreamReader( usersin );   
        	@SuppressWarnings("unchecked")
        	Map<String, User> users = ( Map<String, User> ) TaskService.eval( reader, vars );   
        	for ( User user : users.values() ) {
        		taskSession.addUser( user );
        	}           
        }
        InputStream groupsin = DemoTaskService.class.getResourceAsStream( "LoadGroups.mvel" );
        if(groupsin != null) {
        	Reader reader = new InputStreamReader( groupsin );   
        	@SuppressWarnings("unchecked")
        	Map<String, Group> groups = ( Map<String, Group> ) TaskService.eval( reader, vars );     
        	for ( Group group : groups.values() ) {
        		taskSession.addGroup( group );
        	}
        }
        // try to get the usergroup callback properties
        InputStream usergroupsin = DemoTaskService.class.getResourceAsStream(  "jbpm.usergroup.callback.properties" );
        if(usergroupsin != null) {
        	Properties callbackproperties = new Properties();
        	try {
        	    // Properties.load(Reader) is a JDK 6 method
        		callbackproperties.load(usergroupsin);
        		UserGroupCallbackManager.getInstance().setCallbackFromProperties(callbackproperties);
        		System.out.println("Task service registered usergroup callback ...");
        	} catch (Exception e) {
        		System.out.println("Task service unable to register usergroup callback ...");
        	}
        }
        // start server
        MinaTaskServer server = new MinaTaskServer(taskService);
        Thread thread = new Thread(server);
        thread.start();
        taskSession.dispose();
        System.out.println("Task service started correctly!");
        System.out.println("Task service running ...");
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

}
