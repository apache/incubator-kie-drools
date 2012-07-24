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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


//import org.jbpm.task.service.MockEscalatedDeadlineHandler;
//import org.jbpm.task.service.MockEscalatedDeadlineHandler.Item;

import org.jbpm.task.identity.UserGroupCallbackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.utils.MVELUtils;
import org.jbpm.task.utils.TaskServiceModule;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

public abstract class BaseTest {

    protected static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    protected Map<String, User> users;
    protected Map<String, Group> groups;

    protected static TaskServiceEntryPoint taskService;

    protected boolean useJTA = false;
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private PoolingDataSource pds;
    
    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task");
    }
    @Before
    public void setUp() throws Exception {

//
//        Properties dsProps = loadDataSourceProperties();
//        String txType = dsProps.getProperty("txType", "RESOURCE_LOCAL");
//        if( "RESOURCE_LOCAL".equals(txType) ) { 
//            useJTA = false;
//        } else if( "JTA".equals(txType) ) { 
//            useJTA = true;
//        }
//        
//        if( useJTA ) { 
//            pds = new PoolingDataSource();
//            pds.setUniqueName( "jdbc/taskDS" );
//            pds.setClassName(dsProps.getProperty("className"));
//            pds.setMaxPoolSize(Integer.parseInt(dsProps.getProperty("maxPoolSize")));
//            pds.setAllowLocalTransactions(Boolean.parseBoolean(dsProps.getProperty("allowLocalTransactions")));
//            for (String propertyName : new String[] { "user", "password" }) {
//                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
//            }
//            
//            setDatabaseSpecificDataSourceProperties(pds, dsProps);
//            
//            pds.init();
//        }
        
      

        taskService = TaskServiceModule.getInstance().getTaskService();
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserInfo(userInfo);
        users = fillUsersOrGroups("LoadUsers.mvel");
        groups = fillUsersOrGroups("LoadGroups.mvel");
        taskService.addUsersAndGroups(users, groups);
        disableUserGroupCallback();
        
        logger = LoggerFactory.getLogger(getClass());
        
       
    }
    
    protected Properties loadDataSourceProperties() { 
        String propertiesNotFoundMessage = "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]";

        InputStream propsInputStream = getClass().getResourceAsStream(DATASOURCE_PROPERTIES);
        assertNotNull(propertiesNotFoundMessage, propsInputStream);
        Properties dsProps = new Properties();
        if (propsInputStream != null) {
            try {
                dsProps.load(propsInputStream);
            } catch (IOException ioe) {
                logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        } 
        return dsProps;
    }
    @After
    public void tearDown() throws Exception {
        TaskServiceModule.getInstance().dispose();
        
        if( useJTA ) { 
            pds.close();
        }
    }
    
    public void disableUserGroupCallback() {
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map fillUsersOrGroups(String mvelFileName) throws Exception { 
        Map<String, Object> vars = new HashMap<String, Object>();
        Reader reader = null;
        Map<String, Object> result = null;
        
        try {
            reader = new InputStreamReader(BaseTest.class.getResourceAsStream(mvelFileName));
            result = (Map<String, Object>) MVELUtils.eval(reader, vars);
        } finally {
            if (reader != null) reader.close();
        }
        
        return result;
    }
    
    public static void loadUsersAndGroups(Map<String, User> users, Map<String, Group> groups) throws Exception {
        for (User user : users.values()) {
            taskService.addUser(user);
        }

        for (Group group : groups.values()) {
            taskService.addGroup(group);
        }
    }

    
    
    protected Map<String, Object> fillVariables() { 
        return fillVariables(users, groups);
    }
    
    public static Map<String, Object> fillVariables(Map<String, User> users, Map<String, Group> groups ) { 
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );     
        vars.put( "now", new Date() );
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
    private void setDatabaseSpecificDataSourceProperties(PoolingDataSource pds, Properties dsProps) { 
        String driverClass = dsProps.getProperty("driverClassName");
        if (driverClass.startsWith("org.h2")) {
            for (String propertyName : new String[] { "url", "driverClassName" }) {
                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
            }
        } else {

            if (driverClass.startsWith("oracle")) {
                pds.getDriverProperties().put("driverType", "thin");
                pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
            } else if (driverClass.startsWith("com.ibm.db2")) {
                for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
                pds.getDriverProperties().put("driverType", "4");
            } else if (driverClass.startsWith("com.microsoft")) {
                for (String propertyName : new String[] { "serverName", "portNumber", "databaseName" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
                pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
                pds.getDriverProperties().put("selectMethod", "cursor");
                pds.getDriverProperties().put("InstanceName", "MSSQL01");
            } else if (driverClass.startsWith("com.mysql")) {
                for (String propertyName : new String[] { "databaseName", "serverName", "portNumber", "url" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
            } else if (driverClass.startsWith("com.sybase")) {
                for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
                pds.getDriverProperties().put("REQUEST_HA_SESSION", "false");
                pds.getDriverProperties().put("networkProtocol", "Tds");
            } else if (driverClass.startsWith("org.postgresql")) {
                for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
            } else {
                throw new RuntimeException("Unknown driver class: " + driverClass);
            }
        }
    }
    
    protected final static String mySubject = "My Subject";
    protected final static String myBody = "My Body";
    
    protected static Map<String, String> fillMarshalSubjectAndBodyParams() { 
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", mySubject);
        params.put("body", myBody );
        return params;
    }
    
    protected static void checkContentSubjectAndBody(Object unmarshalledObject) { 
        assertTrue("Content is null." , unmarshalledObject != null && unmarshalledObject.toString() != null);
        String content = unmarshalledObject.toString();
        boolean match = false;
        if( ("{body=" + myBody + ", subject=" + mySubject + "}").equals(content)
            || ("{subject=" + mySubject + ", body=" + myBody + "}").equals(content) ) { 
            match = true;
        }
        assertTrue( "Content does not match.", match );
    }
    
    protected void printTestName() { 
        System.out.println( "Running " + this.getClass().getSimpleName() + "." +  Thread.currentThread().getStackTrace()[2].getMethodName() );
    }
}
