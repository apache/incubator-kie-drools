package org.jbpm.task.service.test.spring;

import static org.jbpm.task.BaseTest.fillUsersOrGroups;
import static org.jbpm.task.service.test.impl.TestServerUtil.*;
import static org.jbpm.task.service.test.spring.BaseSpringTest.removeAllTasks;

import java.util.Map;

import org.h2.tools.Server;
import org.jbpm.task.Group;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.jbpm.task.service.UserGroupCallbackOneImpl;
import org.jbpm.task.service.test.impl.TestTaskServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=("/spring/test-context.xml"))
public class BaseSpringNoUserGroupSetupTest {

    protected static Logger logger;
    
    @Autowired
    private TaskService taskService;
    
    protected TaskServiceSession taskSession;
    protected TaskServer server;
    protected TaskClient client;
    
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    
    private static Server dbServer;
    
    @Before
    public void setUp() throws Exception {
        removeAllTasks();
        
        logger = LoggerFactory.getLogger(getClass());
        
        // Finish setting up the taskService to test
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        
        // Add users and groups to session
        users = fillUsersOrGroups("LoadUsers.mvel");
        groups = fillUsersOrGroups("LoadGroups.mvel");
        taskService.addUsersAndGroups(users, groups);
        
        // Client/Server setup
        server = startServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();
        
        taskSession = taskService.createSession();
        
        // Setup User Group Call back
        if(!UserGroupCallbackManager.getInstance().existsCallback()) {
            UserGroupCallbackManager.getInstance().setCallback(new UserGroupCallbackOneImpl());
        }
        taskSession.addUser(new User("Administrator"));

    }
    
    @After
    public void after() throws Exception {
        taskSession.dispose();
        client.disconnect();
        server.stop();
    }    
    
    @Test
    public void dummy() { 
        
    }
    
}
