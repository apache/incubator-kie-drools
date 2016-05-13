/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.test.util.db;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger( PersistenceUtil.class );

    public static final String ENTITY_MANAGER_FACTORY = "org.kie.api.persistence.jpa.EntityManagerFactory";
    public static final String TRANSACTION_MANAGER = "TRANSACTION_MANAGER";

    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private static H2Server h2Server = new H2Server();

    private static Properties defaultProperties = null;

    // Setup and marshalling setup constants
    public static String DATASOURCE = "org.droolsjbpm.persistence.datasource";

    /**
     * @see #setupWithPoolingDataSource(String, String, boolean)
     * @param persistenceUnitName The name of the persistence unit to be used.
     * @return test context
     */
    public static Map<String, Object> setupWithPoolingDataSource(String persistenceUnitName) {
        return setupWithPoolingDataSource(persistenceUnitName, true);
    }

    /**
     * @see #setupWithPoolingDataSource(String, String, boolean)
     * @param persistenceUnitName The name of the persistence unit to be used.
     * @return test context
     */
    public static Map<String, Object> setupWithPoolingDataSource(String persistenceUnitName, boolean testMarshalling) {
        return setupWithPoolingDataSource(persistenceUnitName, "jdbc/testDS1", testMarshalling);
    }

    /**
     * This method does all of the setup for the test and returns a HashMap
     * containing the persistence objects that the test might need.
     *
     * @param persistenceUnitName
     *            The name of the persistence unit used by the test.
     * @return Map with persistence objects, such as the EntityManagerFactory and DataSource
     */
    public static Map<String, Object> setupWithPoolingDataSource(final String persistenceUnitName, String dataSourceName, final boolean testMarshalling) {
        // set the right jdbc url
        Properties dsProps = getDatasourceProperties();
        String jdbcUrl = dsProps.getProperty("url");
        String driverClass = dsProps.getProperty("driverClassName");

        // Setup the datasource
        PoolingDataSource ds1 = setupPoolingDataSource(dsProps, dataSourceName);
        if( driverClass.startsWith("org.h2") ) {
            jdbcUrl += "tcp://localhost/target/persistence-test";
            ds1.getDriverProperties().setProperty("url", jdbcUrl);
        }
        ds1.init();

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(DATASOURCE, ds1);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        context.put(ENTITY_MANAGER_FACTORY, emf);
        context.put(TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());

        return context;
    }

    /**
     * This method should be called in the @After method of a test to clean up
     * the persistence unit and datasource.
     *
     * @param context
     *            A HashMap
     *
     */
    public static void cleanUp(Map<String, Object> context) {
        if (context != null) {

            BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
            if( txm != null ) {
                txm.shutdown();
            }

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
     * This method uses the "jdbc/testDS1" datasource, which is the default.
     * @param dsProps The properties used to setup the data source.
     * @return a PoolingDataSource
     */
    public static PoolingDataSource setupPoolingDataSource(Properties dsProps) {
        return setupPoolingDataSource(dsProps, "jdbc/testDS1");
    }

    /**
     * This sets up a Bitronix PoolingDataSource.
     *
     * @return PoolingDataSource that has been set up but _not_ initialized.
     */
    public static PoolingDataSource setupPoolingDataSource(Properties dsProps, String datasourceName) {
        PoolingDataSource pds = new PoolingDataSource();

        // The name must match what's in the persistence.xml!
        pds.setUniqueName(datasourceName);

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
        } else {
            pds.setClassName(dsProps.getProperty("className"));

            if (driverClass.startsWith("oracle")) {
                pds.getDriverProperties().put("driverType", "thin");
                pds.getDriverProperties().put("URL", dsProps.getProperty("url"));
            } else if (driverClass.startsWith("com.ibm.db2")) {
                // http://docs.codehaus.org/display/BTM/JdbcXaSupportEvaluation#JdbcXaSupportEvaluation-IBMDB2
                pds.getDriverProperties().put("databaseName", dsProps.getProperty("databaseName"));
                pds.getDriverProperties().put("driverType", "4");
                pds.getDriverProperties().put("serverName", dsProps.getProperty("serverName"));
                pds.getDriverProperties().put("portNumber", dsProps.getProperty("portNumber"));
                pds.getDriverProperties().put("currentSchema", dsProps.getProperty("defaultSchema"));
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
            } else if (driverClass.startsWith("org.mariadb")) {
                for (String propertyName : new String[] { "databaseName", "serverName", "portNumber", "url" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
            } else if (driverClass.startsWith("com.sybase")) {
                for (String propertyName : new String[] { "databaseName", "portNumber", "serverName" }) {
                    pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
                }
                pds.getDriverProperties().put("REQUEST_HA_SESSION", "false");
                pds.getDriverProperties().put("networkProtocol", "Tds");
            } else if (driverClass.startsWith("org.postgresql") || driverClass.startsWith("com.edb")) {
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

        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");

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
        if (databaseDriverClassName.startsWith("org.postgresql") || databaseDriverClassName.startsWith("com.edb")) {
            useTransactions = true;
        }
        return useTransactions;
    }

    /**
     * An class responsible for starting and stopping the H2 database (tcp)
     * server
     */
    private static class H2Server {
        private Server realH2Server;

        public void start() {
            if (realH2Server == null || !realH2Server.isRunning(false)) {
                try {
                    DeleteDbFiles.execute("", "JPADroolsFlow", true);
                    realH2Server = Server.createTcpServer(new String[0]);
                    realH2Server.start();
                } catch (SQLException e) {
                    throw new RuntimeException("Can't start h2 server db", e);
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

}
