/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.utils.MVELUtils;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

public abstract class BaseTest {

    protected static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    
    @Inject
    protected TaskServiceEntryPoint taskService;

    @Before
    public void setUp() {
        try {
            System.out.println(" XXXXXXX GOING UP!");
            MockUserInfo userInfo = new MockUserInfo();
            taskService.setUserInfo(userInfo);

            users = fillUsersOrGroups("LoadUsers.mvel");
            groups = fillUsersOrGroups("LoadGroups.mvel");
            taskService.addUsersAndGroups(users, groups);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TaskServiceLifeCycleBaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @After
    public void tearDown() {
        System.out.println("XXXXXXXXXX Going Down! ");
    }

    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map fillUsersOrGroups(String mvelFileName) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Reader reader = null;
        Map<String, Object> result = null;

        try {
            reader = new InputStreamReader(BaseTest.class.getResourceAsStream(mvelFileName));
            result = (Map<String, Object>) MVELUtils.eval(reader, vars);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result;
    }

    protected Map<String, Object> fillVariables() {
        return fillVariables(users, groups);
    }

    public static Map<String, Object> fillVariables(Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());
        return vars;
    }
//    protected static void testDeadlines(long now, MockEscalatedDeadlineHandler handler) throws Exception { 
//        int sleep = 8000;
//        handler.wait(3, sleep);
//
//        assertEquals(3, handler.getList().size());
//
//        boolean firstDeadlineMet = false;
//        boolean secondDeadlineMet = false;
//        boolean thirdDeadlineMet = false;
//        for( Item item : handler.getList() ) { 
//            long deadlineTime = item.getDeadline().getDate().getTime();
//            if( deadlineTime == now + 2000 ) { 
//                firstDeadlineMet = true;
//            }
//            else if( deadlineTime == now + 4000 ) { 
//                secondDeadlineMet = true;
//            }
//            else if( deadlineTime == now + 6000 ) { 
//                thirdDeadlineMet = true;
//            }
//            else { 
//                fail( deadlineTime + " is not an expected deadline time. Now is [" + now + " (" + (deadlineTime-now) + ")]." );
//            }
//        }
//        
//        assertTrue( "First deadline was not met." , firstDeadlineMet );
//        assertTrue( "Second deadline was not met." , secondDeadlineMet );
//        assertTrue( "Third deadline was not met." , thirdDeadlineMet );   
//        
//        // Wait for deadlines to finish
//        Thread.sleep(1000); 
//    }
//    
    protected final static String mySubject = "My Subject";
    protected final static String myBody = "My Body";

    protected static Map<String, String> fillMarshalSubjectAndBodyParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", mySubject);
        params.put("body", myBody);
        return params;
    }

    protected static void checkContentSubjectAndBody(Object unmarshalledObject) {
        assertTrue("Content is null.", unmarshalledObject != null && unmarshalledObject.toString() != null);
        String content = unmarshalledObject.toString();
        boolean match = false;
        if (("{body=" + myBody + ", subject=" + mySubject + "}").equals(content)
                || ("{subject=" + mySubject + ", body=" + myBody + "}").equals(content)) {
            match = true;
        }
        assertTrue("Content does not match.", match);
    }

    protected void printTestName() {
        System.out.println("Running " + this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
    }
}
