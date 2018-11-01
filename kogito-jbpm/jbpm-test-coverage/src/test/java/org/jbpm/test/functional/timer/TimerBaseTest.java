/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.timer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.test.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimerBaseTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TimerBaseTest.class);
	
	private static PoolingDataSource pds;
    
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    
    protected static final String MAX_POOL_SIZE = "maxPoolSize";
    protected static final String ALLOW_LOCAL_TXS = "allowLocalTransactions";
    
    protected static final String DATASOURCE_CLASS_NAME = "className";
    protected static final String DRIVER_CLASS_NAME = "driverClassName";
    protected static final String USER = "user";
    protected static final String PASSWORD = "password";
    protected static final String JDBC_URL = "url";
    
    public static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = getDatasourceProperties();
        PoolingDataSource pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        try {
        	pds.init();
        } catch (Exception e) {
        	logger.warn("DBPOOL_MGR:Looks like there is an issue with creating db pool because of " + e.getMessage() + " cleaing up...");
            try {
                pds.close();
            } catch (Exception ex) {
                // ignore
            }
        	logger.info("DBPOOL_MGR: attempting to create db pool again...");
        	pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        	pds.init();        	
        	logger.info("DBPOOL_MGR:Pool created after cleanup of leftover resources");
        }
        
        return pds;
    }
    
    
    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     * 
     * @return Properties containing the datasource properties.
     */
    private static Properties getDatasourceProperties() { 
        boolean propertiesNotFound = false;
        
        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");
        
        InputStream propsInputStream = TimerBaseTest.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        } else {
            propertiesNotFound = true;
        }

        String password = props.getProperty("password");
        if ("${maven.jdbc.password}".equals(password) || propertiesNotFound) {
           logger.warn( "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]" );
        }
        
        // If maven filtering somehow doesn't work the way it should.. 
        setDefaultProperties(props);

        return props;
    }

    /**
     * Return the default database/datasource properties - These properties use
     * an in-memory H2 database
     * 
     * This is used when the developer is somehow running the tests but
     * bypassing the maven filtering that's been turned on in the pom.
     * 
     * @return Properties containing the default properties
     */
    private static void setDefaultProperties(Properties props) {
        String[] keyArr = { 
                "serverName", "portNumber", "databaseName", JDBC_URL,
                USER, PASSWORD,
                DRIVER_CLASS_NAME, DATASOURCE_CLASS_NAME,
                MAX_POOL_SIZE, ALLOW_LOCAL_TXS };
        String[] defaultPropArr = { 
                "", "", "", "jdbc:h2:mem:jbpm-db;MVCC=true",
                "sa", "", 
                "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", 
                "5", "true" };
        Assert.assertTrue("Unequal number of keys for default properties", keyArr.length == defaultPropArr.length);
        for (int i = 0; i < keyArr.length; ++i) {
            if( ! props.containsKey(keyArr[i]) ) {
                props.put(keyArr[i], defaultPropArr[i]);
            }
        }
    }   
    
    @BeforeClass
    public static void setUpOnce() {
        if (pds == null) {
            pds = setupPoolingDataSource();
        }
    }
    
    @AfterClass
    public static void tearDownOnce() {
        if (pds != null) {
            pds.close();
            pds = null;
        }
    }
    

    protected void testCreateQuartzSchema() {
        Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/quartz_tables_h2.sql")).useDelimiter(";");
        try {
            Connection connection = ((DataSource)InitialContext.doLookup("jdbc/jbpm-ds")).getConnection();
            Statement stmt = connection.createStatement();
            while (scanner.hasNext()) {
                String sql = scanner.next();
                stmt.executeUpdate(sql);
            }
            stmt.close();
            connection.close();
        } catch (Exception e) {
            
        }
    }
    
    protected class TestRegisterableItemsFactory extends DefaultRegisterableItemsFactory {
        private ProcessEventListener[] plistener;
        private AgendaEventListener[] alistener;
        private TaskLifeCycleEventListener[] tlistener;
        
        public TestRegisterableItemsFactory(ProcessEventListener... listener) {
            this.plistener = listener;
        }
        
        public TestRegisterableItemsFactory(AgendaEventListener... listener) {
            this.alistener = listener;
        }
        
        public TestRegisterableItemsFactory(TaskLifeCycleEventListener... tlistener) {
            this.tlistener = tlistener;
        }

        @Override
        public List<ProcessEventListener> getProcessEventListeners(
                RuntimeEngine runtime) {
            
            List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
            if (plistener != null) {
                listeners.addAll(Arrays.asList(plistener));
            }
            
            return listeners;
        }
        @Override
        public List<AgendaEventListener> getAgendaEventListeners(
                RuntimeEngine runtime) {
            
            List<AgendaEventListener> listeners = super.getAgendaEventListeners(runtime);
            if (alistener != null) { 
                listeners.addAll(Arrays.asList(alistener));
            }
            
            return listeners;
        }

        @Override
        public List<TaskLifeCycleEventListener> getTaskListeners() {

            List<TaskLifeCycleEventListener> listeners = super.getTaskListeners();
            if (tlistener != null) {
                listeners.addAll(Arrays.asList(tlistener));
            }
            return listeners;
        } 
        
    }
}
