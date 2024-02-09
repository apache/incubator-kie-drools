/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.test.util.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger( PersistenceUtil.class );

    public static final String ENTITY_MANAGER_FACTORY = "org.kie.api.persistence.jpa.EntityManagerFactory";
    public static final String TRANSACTION_MANAGER = "java:comp/TransactionManager";

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
        // Setup the datasource
        PoolingDataSourceWrapper ds1 = setupPoolingDataSource(getDatasourceProperties(), dataSourceName);

        Map<String, Object> context = new HashMap<>();
        context.put(DATASOURCE, ds1);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        context.put(ENTITY_MANAGER_FACTORY, emf);

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

            Object emfObject = context.remove(ENTITY_MANAGER_FACTORY);
            if (emfObject != null) {
                try {
                    EntityManagerFactory emf = (EntityManagerFactory) emfObject;
                    emf.close();
                } catch (Throwable t) {
                    logger.error("Exception", t);
                }
            }

            Object ds1Object = context.remove(DATASOURCE);
            if (ds1Object != null) {
                try {
                    PoolingDataSourceWrapper ds1 = (PoolingDataSourceWrapper) ds1Object;
                    ds1.close();
                } catch (Throwable t) {
                    logger.error("Exception", t);
                }
            }

        }

    }

    /**
     * This method creates default pooling datasource
     * @return a PoolingDataSource
     */
    public static PoolingDataSourceWrapper setupPoolingDataSource() {
        return setupPoolingDataSource(getDatasourceProperties());
    }

    /**
     * This method uses the "jdbc/testDS1" datasource, which is the default.
     * @param dsProps The properties used to setup the data source.
     * @return a PoolingDataSource
     */
    public static PoolingDataSourceWrapper setupPoolingDataSource(Properties dsProps) {
        String datasourceName = dsProps.getProperty("datasourceName", "jdbc/testDS1");
        return setupPoolingDataSource(dsProps, datasourceName);
    }

    /**
     * This sets up a PoolingDataSource.
     *
     * @return PoolingDataSource that has been set up but _not_ initialized.
     */
    public static PoolingDataSourceWrapper setupPoolingDataSource(Properties dsProps, String datasourceName) {
        String driverClass = dsProps.getProperty("driverClassName");

        if (driverClass.startsWith("org.h2")) {
            String jdbcUrl = dsProps.getProperty("url");
            // fix an incomplete JDBC URL used by some tests
            if (jdbcUrl.startsWith("jdbc:h2:") && !jdbcUrl.contains("tcp://") && !jdbcUrl.contains("mem:")) {
                dsProps.put("url", jdbcUrl + "tcp://localhost/target/./persistence-test");
            }

            h2Server.start(dsProps.getProperty("h2Args"));
        }
        return DataSourceFactory.setupPoolingDataSource(datasourceName, dsProps);
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
                    "org.h2.jdbcx.JdbcDataSource", "16", "true" };
            if (keyArr.length != defaultPropArr.length) {
                throw new IllegalStateException("Unequal number of keys for default properties!");
            }
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
        if (propsInputStream == null) {
            throw new IllegalStateException(propertiesNotFoundMessage);
        }
        Properties props = new Properties();
        try {
            props.load(propsInputStream);
        } catch (IOException ioe) {
            propertiesNotFound = true;
            logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
            logger.error("Exception", ioe);
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
     * A class responsible for starting and stopping the H2 database (tcp)
     * server
     */
    private static class H2Server {
        private Server realH2Server;

        /**
         * Starts the H2 server in the TCP mode.
         * @param h2Args startup arguments separated by white chars; if {@code null} or empty, no arguments are passed
         */
        public void start(String h2Args) {
            System.out.println("running H2 server");
            if (realH2Server == null || !realH2Server.isRunning(false)) {
                try {
                    DeleteDbFiles.execute("", "JPADroolsFlow", true);
                    realH2Server = Server.createTcpServer(parseArgs(h2Args));
                    realH2Server.start();
                } catch (SQLException e) {
                    throw new RuntimeException("Can't start h2 server db", e);
                }
            }
        }

        private String [] parseArgs(String args) {
            if (args == null || args.trim().isEmpty()) {
                return new String[0];
            }

            return args.trim().split("\\s+");
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
