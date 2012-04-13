package org.jbpm.task.service.test.spring;

import static org.jbpm.task.BaseTest.*;
import static org.jbpm.task.service.test.impl.TestServerUtil.*;

import java.util.Map;

import org.jbpm.task.Group;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.jbpm.task.service.test.impl.TestTaskServer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=("/spring/test-context.xml"))
public abstract class BaseSpringTest {

    protected static Logger logger;
    
    @Autowired
    private TaskService taskService;
    
    protected TaskServer server;
    protected TaskClient client;
    
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    
    @Before
    public void setUp() throws Exception {
        logger = LoggerFactory.getLogger(getClass());
        
        // Finish setting up the taskService to test
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        
        // Add users and groups to sessoin
        TaskServiceSession taskSession = taskService.createSession();
        users = fillUsersOrGroups("LoadUsers.mvel");
        groups = fillUsersOrGroups("LoadGroups.mvel");
        loadUsersAndGroups(taskSession, users, groups);
        
        // Disable User Group Call back
        UserGroupCallbackManager.getInstance().setCallback(null);
        
        // Client/Server setup
        server = startServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();
    }

    @After
    public void tearDown() throws Exception {
        client.disconnect();
        server.stop();
    }    


}
