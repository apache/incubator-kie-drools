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

import static org.drools.marshalling.util.MarshallingDBUtil.*;
import static org.drools.marshalling.util.MarshallingTestUtil.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.drools.marshalling.util.EntityManagerFactoryProxyFactory;
import org.drools.marshalling.util.MarshallingDBUtil;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger( PersistenceUtil.class );

    private static boolean TEST_MARSHALLING = true;
    
    // Persistence and data source constants
    public static final String DROOLS_PERSISTENCE_UNIT_NAME = "org.drools.persistence.jpa";
    public static final String JBPM_PERSISTENCE_UNIT_NAME = "org.jbpm.persistence.jpa";
        
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private static TestH2Server h2Server = new TestH2Server();
    
    private static Properties defaultProperties = null;
   
    // Setup and marshalling setup constants
    public static String DATASOURCE = "org.drools.persistence.datasource";
    public static String ENTITY_MANAGER_FACTORY = "org.drools.persistence.entityManagerFactory";

    /**
     * @see #setupWithPoolingDataSource(String, boolean)
     * @param persistenceUnitName The name of the persistence unit to be used.
     * @return test context
     */
    public static HashMap<String, Object> setupWithPoolingDataSource(String persistenceUnitName) {
        return setupWithPoolingDataSource(persistenceUnitName, TEST_MARSHALLING);
    }
    
    /**
     * This method does all of the setup for the test and returns a HashMap
     * containing the persistence objects that the test might need.
     * 
     * @param persistenceUnitName
     *            The name of the persistence unit used by the test.
     * @return HashMap<String Object> with persistence objects, such as the
     *         EntityManagerFactory and DataSource
     */
    public static HashMap<String, Object> setupWithPoolingDataSource(String persistenceUnitName, boolean testMarshalling) {
        HashMap<String, Object> context = new HashMap<String, Object>();

        // set the right jdbc url
        Properties dsProps = getDatasourceProperties();
        String jdbcUrl = dsProps.getProperty("url");
        String driverClass = dsProps.getProperty("driverClassName");

        // only save marshalling data if the dialect is H2..
        if( ! driverClass.startsWith("org.h2") ) { 
           testMarshalling = false; 
        }
        if( testMarshalling ) {
            Class<?> testClass = null;
            StackTraceElement [] ste = Thread.currentThread().getStackTrace();
                // Sorry, I've always wanted to use a do/while loop.. :) 
                int i = 1;
                do { 
                    try {
                        testClass = Class.forName(ste[i++].getClassName());
                    } catch (ClassNotFoundException e) {
                        // do nothing.. 
                    }
                } while ( PersistenceUtil.class.equals(testClass) && i < ste.length );
                assertNotNull("Unable to resolve test class!", testClass);
                
            jdbcUrl = initializeTestDb(dsProps, testClass);
        }
        else { 
            jdbcUrl += "tcp://localhost/JPADroolsFlow";
        }

        // Setup the datasource
        PoolingDataSource ds1 = setupPoolingDataSource(dsProps);
        ds1.getDriverProperties().setProperty("url", jdbcUrl);
        ds1.init();
        context.put(DATASOURCE, ds1);

        // Setup persistence
        EntityManagerFactory emf;
        if (testMarshalling) {
            Properties overrideProperties = new Properties();
            overrideProperties.setProperty("hibernate.connection.url", jdbcUrl);
            EntityManagerFactory realEmf = Persistence.createEntityManagerFactory(persistenceUnitName, overrideProperties);
            emf = (EntityManagerFactory) EntityManagerFactoryProxy.newInstance(realEmf);
           
            UserTransaction ut = (UserTransaction) UserTransactionProxy.newInstance(realEmf);
            context.put(TRANSACTION, ut);
        } else {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        }
        
        context.put(ENTITY_MANAGER_FACTORY, emf);

        return context;
    }

    /**
     * This method should be called in the @After method of a test to clean up
     * the persistence unit and datasource.
     * 
     * @param context
     *            A HashMap generated by
     *            {@link org.drools.persistence.util.PersistenceUtil setupWithPoolingDataSource(String)}
     * 
     */
    public static void tearDown(HashMap<String, Object> context) {
        if (context != null) {
            
            Object emfObject = context.remove(ENTITY_MANAGER_FACTORY);
            if (emfObject != null) {
                try {
                    EntityManagerFactory emf = (EntityManagerFactory) emfObject;
                    emf.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            Object ds1Object = context.remove(DATASOURCE);
            if (ds1Object != null) {
                try {
                    PoolingDataSource ds1 = (PoolingDataSource) ds1Object;
                    ds1.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            
        }
        
    }
    
    /**
     * This sets up a Bitronix PoolingDataSource.
     * 
     * @return PoolingDataSource that has been set up but _not_ initialized.
     */
    public static PoolingDataSource setupPoolingDataSource(Properties dsProps) {
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
            if (driverClass.startsWith("oracle")) {
                pds.getDriverProperties().put("driverType", "thin");
                pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
            } else if (driverClass.startsWith("com.ibm.db2")) {
                // placeholder for eventual future modifications
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

        return pds;
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
    private static Properties getDefaultProperties() {
        if (defaultProperties == null) {
            String[] keyArr = { "serverName", "portNumber", "databaseName", "url", "user", "password", "driverClassName",
                    "className", "maxPoolSize", "allowLocalTransactions" };
            String[] defaultPropArr = { "", "", "", "jdbc:h2:tcp://localhost/JPADroolsFlow", "sa", "", "org.h2.Driver",
                    "bitronix.tm.resource.jdbc.lrc.LrcXADataSource", "16", "true" };
            Assert.assertTrue("Unequal number of keys for default properties", keyArr.length == defaultPropArr.length);
            defaultProperties = new Properties();
            for (int i = 0; i < keyArr.length; ++i) {
                defaultProperties.put(keyArr[i], defaultPropArr[i]);
            }
        }

        return defaultProperties;
    }

    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     * 
     * @return Properties containing the datasource properties.
     */
    public static Properties getDatasourceProperties() { 
        String propertiesNotFoundMessage = "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]";
        boolean propertiesNotFound = false;

        // OCRAM: PersistenceUtil.getDatasourceProperties: check that this works when PU is in a jar.. 
        InputStream propsInputStream = PersistenceUtil.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        assertNotNull(propertiesNotFoundMessage, propsInputStream);
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
            props = getDefaultProperties();
        }

        return props;
    }

    /**
     * This method returns whether or not transactions should be used when
     * dealing with the SessionInfo object (or any other persisted entity that
     * contains @Lob's )
     * 
     * @return boolean Whether or not to use transactions
     */
    public static boolean useTransactions() {
        boolean useTransactions = false;
        String databaseDriverClassName = getDatasourceProperties().getProperty("driverClassName");

        // Postgresql has a "Large Object" api which REQUIRES the use of transactions
        //  since @Lob/byte array is actually stored in multiple tables.
        if (databaseDriverClassName.startsWith("org.postgresql")) {
            useTransactions = true;
        }
        return useTransactions;
    }

    /**
     * An class responsible for starting and stopping the H2 database (tcp)
     * server
     */
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

    /**
     * Reflection method when doing ugly hacks in tests.
     * 
     * @param fieldname
     *            The name of the field to be retrieved.
     * @param source
     *            The object containing the field to be retrieved.
     * @return The value (object instance) stored in the field requested from
     *         the given source object.
     */
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

    private Environment createEnvironment(HashMap<String, Object> context) { 
        Environment env = EnvironmentFactory.newEnvironment();
        
        UserTransaction ut = (UserTransaction) context.get(TRANSACTION);
        if( ut != null ) { 
            env.set( TRANSACTION, ut);
        }
        
        env.set( ENTITY_MANAGER_FACTORY, context.get(ENTITY_MANAGER_FACTORY) );
        env.set( TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
        env.set( GLOBALS, new MapGlobalResolver() );
        
        return env;
    }
    
}
