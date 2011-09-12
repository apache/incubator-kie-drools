/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Properties;

import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger( PersistenceUtil.class );

    public static final String DROOLS_PERSISTENCE_UNIT_NAME = "org.drools.persistence.jpa";
    public static final String JBPM_PERSISTENCE_UNIT_NAME = "org.jbpm.persistence.jpa";
        
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private static TestH2Server h2Server = new TestH2Server();
    
    private static Properties defaultProperties = null;
   
    private static Properties getDefaultProperties() { 
        if( defaultProperties == null ) { 
            String [] keyArr = { "serverName", "portNumber", "databaseName", "url", 
                    "user", "password", "driverClassName", "className", 
                    "maxPoolSize", "allowLocalTransactions" };
            String [] defaultPropArr= { "", "", "", "jdbc:h2:tcp://localhost/JPADroolsFlow", 
                    "sa", "", "org.h2.Driver", "bitronix.tm.resource.jdbc.lrc.LrcXADataSource", 
                    "16", "true" };
            Assert.assertTrue("Unequal number of keys for default properties", keyArr.length == defaultPropArr.length);
            defaultProperties = new Properties();
            for( int i = 0; i < keyArr.length; ++i ) { 
                defaultProperties.put(keyArr[i], defaultPropArr[i]);
            }
        }
        
        return defaultProperties;
    }

    public static Properties getDatasourceProperties() {
        boolean propertiesNotFound = false;

        InputStream propsInputStream = PersistenceUtil.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        Properties props = new Properties();
        if( propsInputStream != null ) { 
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                propertiesNotFound = true;
                logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
        else { 
            propertiesNotFound = true;
        }

        String password = props.getProperty("password");
        if( "${maven.jdbc.password}".equals(password) || propertiesNotFound )  { 
           props = getDefaultProperties();
        }
            
        return props;
    }

    public static PoolingDataSource setupPoolingDataSource() {
        Properties dsProps = getDatasourceProperties();

        PoolingDataSource pds = new PoolingDataSource();

        // The name must match what's in the persistence.xml!
        pds.setUniqueName("jdbc/testDS1");

        pds.setClassName(dsProps.getProperty("className"));

        pds.setMaxPoolSize(Integer.parseInt(dsProps.getProperty("maxPoolSize")));
        pds.setAllowLocalTransactions(Boolean.parseBoolean(dsProps
                .getProperty("allowLocalTransactions")));
        for (String propertyName : new String[] { "user", "password" }) {
            pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
        }

        String driverClass = dsProps.getProperty("driverClassName");
        if (driverClass.startsWith("org.h2")) {
            h2Server.start();
            for (String propertyName : new String[] { "url", "driverClassName" }) {
                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
            }
        }
        else { 
            pds.setClassName(dsProps.getProperty("className"));
            
            if( driverClass.startsWith("oracle") ) {
	            pds.getDriverProperties().put("driverType", "thin");
	            pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
	        }
	        else if( driverClass.startsWith("com.ibm.db2") ) { 
	            // placeholder for eventual future modifications
	        }
	        else if( driverClass.startsWith("com.microsoft") ) { 
	            for (String propertyName : new String[] { "serverName", "portNumber", "databaseName" }) {
	                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
	            }
	            pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
	            pds.getDriverProperties().put("selectMethod", "cursor");
	            pds.getDriverProperties().put("InstanceName", "MSSQL01");
	            // pds.getDriverProperties().put("instanceName", dsProps.getProperty("databaseName"));
	            // do nothing
	            // pds.getDriverProperties().put("instanceName", "mssql");
	        }
	        else if( driverClass.startsWith("com.mysql") ) { 
	            for (String propertyName : new String[] { "databaseName", "serverName", "portNumber", "url" }) {
	                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
	            }
	        }
	        else if( driverClass.startsWith("com.sybase") ) { 
	            for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
	                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
	            }
	            pds.getDriverProperties().put("REQUEST_HA_SESSION", "false");
	            pds.getDriverProperties().put("networkProtocol", "Tds");
	        }
	        else if( driverClass.startsWith("org.postgresql") ) { 
	            for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
	                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
	            }
	        }
	        else { 
	            throw new RuntimeException("Unknown driver class: " + driverClass);
	        }
        }

        return pds;
    }
    
    public static boolean useTransactions() { 
        boolean useTransactions = false;
        String databaseDriverClassName =  getDatasourceProperties().getProperty("driverClassName");
        
        // Postgresql has a "Large Object" api which REQUIRES the use of transactions, since 
        //  @Lob/byte array is actually stored in multiple tables. 
        if( databaseDriverClassName.startsWith("org.postgresql") ) { 
            useTransactions = true;
        }
        return useTransactions;
    }

    private static class TestH2Server {
        private Server realH2Server;

        public void start() {
            if (realH2Server == null || !realH2Server.isRunning(false)) {
                try {
                    DeleteDbFiles.execute("", "JPADroolsFlow", true);
                    realH2Server = Server.createTcpServer(new String[0]);
                    realH2Server.start();
                } catch (SQLException e) {
                    throw new RuntimeException("can't start h2 server db", e);
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            if (realH2Server != null) {
                realH2Server.stop();
            }
            DeleteDbFiles.execute("", "JPADroolsFlow", true);
            super.finalize();
        }

    }
    
    public static Object getValueOfField(String fieldname, Object source) {
        String sourceClassName = source.getClass().getSimpleName();
    
        Field field = null;
        try {
            field = source.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
        } catch (SecurityException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        } catch (NoSuchFieldException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        }
    
        assertNotNull("." + fieldname + " field is null!?!", field);
        Object fieldValue = null;
        try {
            fieldValue = field.get(source);
        } catch (IllegalArgumentException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        } catch (IllegalAccessException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        }
        return fieldValue;
    }

}
