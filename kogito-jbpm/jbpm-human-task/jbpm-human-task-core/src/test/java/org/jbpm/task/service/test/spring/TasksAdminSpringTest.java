package org.jbpm.task.service.test.spring;

import static org.jbpm.task.admin.TasksAdminTest.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=("/spring/test-context.xml"))
public class TasksAdminSpringTest extends BaseSpringTest {

protected static Logger logger;
    
    @Autowired
    protected TaskService taskService;
    
    protected TaskServiceSession taskSession;
    protected EntityManagerFactory emf;
    
    private Map<String, User> users = new HashMap<String, User>();
    private Map<String, Group> groups = new HashMap<String, Group>();
    
    @Before
    public void before() { 
        emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());

        taskSession = taskService.createSession();

        addUsersAndGroups(taskSession, users, groups);

        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
    
    @Test
    public void completedSinceTasksTest() { 
       runCompletedSinceTasksTest(users, taskService); 
    }
    
    @Test
    public void archiveTasksTest() { 
       runArchiveTasksTest(users, taskService, emf);
    }
    
    @Test
    public void completedTasksTest() { 
       runCompletedTasksTest(users, taskService);
    }
    
    @Test
    public void removeTasksTest() { 
       runRemoveTasksTest(users, taskService, emf);
    }
    
}
