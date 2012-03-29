/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.service.MockEscalatedDeadlineHandler;
import org.jbpm.task.service.MockEscalatedDeadlineHandler.Item;
import org.jbpm.task.service.SendIcal;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest extends TestCase {

    protected Logger logger;
    
    protected EntityManagerFactory emf;

    protected Map<String, User> users;
    protected Map<String, Group> groups;

    protected TaskService taskService;
    protected TaskServiceSession taskSession;

    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task");
    }
    
    protected void setUp() throws Exception {
        Properties conf = new Properties();
        conf.setProperty("mail.smtp.host", "localhost");
        conf.setProperty("mail.smtp.port", "2345");
        conf.setProperty("from", "from@domain.com");
        conf.setProperty("replyTo", "replyTo@domain.com");
        conf.setProperty("defaultLanguage", "en-UK");
        SendIcal.initInstance(conf);

        // Use persistence.xml configuration
        emf = createEntityManagerFactory();

        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        loadUsersAndGroups(taskService);
        disableUserGroupCallback();
        
        logger = LoggerFactory.getLogger(getClass());
    }

    protected void tearDown() throws Exception {
        taskSession.dispose();
        emf.close();
    }
    
    public void disableUserGroupCallback() {
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
    
    public void loadUsersAndGroups(TaskService taskService) throws Exception {
        Map vars = new HashMap();

        Reader reader = null;

        try {
            reader = new InputStreamReader(BaseTest.class.getResourceAsStream("LoadUsers.mvel"));
            users = (Map<String, User>) eval(reader, vars);
            for (User user : users.values()) {
                taskSession.addUser(user);
            }
        } finally {
            if (reader != null) reader.close();
            reader = null;
        }

        try {
            reader = new InputStreamReader(BaseTest.class.getResourceAsStream("LoadGroups.mvel"));
            groups = (Map<String, Group>) eval(reader,  vars);
            for (Group group : groups.values()) {
                taskSession.addGroup(group);
            }
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static Object eval(Reader reader, Map vars) {
        vars.put("now", new Date());
        return TaskService.eval(reader, vars);
    }
    
    public Object eval(String str, Map vars) {
        vars.put("now", new Date());
        return TaskService.eval(str, vars);
    }
    
    protected Map<String, Object> fillVariables() { 
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        return vars;
    }
    
    protected static void testDeadlines(long now, MockEscalatedDeadlineHandler handler) throws Exception { 
        int sleep = 8000;
        handler.wait(3, sleep);

        assertEquals(3, handler.getList().size());

        boolean firstDeadlineMet = false;
        boolean secondDeadlineMet = false;
        boolean thirdDeadlineMet = false;
        for( Item item : handler.getList() ) { 
            long deadlineTime = item.getDeadline().getDate().getTime();
            if( deadlineTime == now + 2000 ) { 
                firstDeadlineMet = true;
            }
            else if( deadlineTime == now + 4000 ) { 
                secondDeadlineMet = true;
            }
            else if( deadlineTime == now + 6000 ) { 
                thirdDeadlineMet = true;
            }
            else { 
                fail( deadlineTime + " is not an expected deadline time." );
            }
        }
        
        assertTrue( "First deadline was not met." , firstDeadlineMet );
        assertTrue( "Second deadline was not met." , secondDeadlineMet );
        assertTrue( "Third deadline was not met." , thirdDeadlineMet );   
        
        // Wait for deadlines to finish
        Thread.sleep(1000);
    }
}
