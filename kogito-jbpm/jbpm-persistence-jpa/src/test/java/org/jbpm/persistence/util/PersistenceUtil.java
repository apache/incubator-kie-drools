package org.jbpm.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.Assert;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistenceUtil {

    public static final String PERSISTENCE_UNIT_NAME = "org.jbpm.persistence.jpa";
        
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    private static TestH2Server h2Server = new TestH2Server();

    private static Properties getDatasourceProperties() {
        String propertiesNotFound = "Unable to load datasource properties ["+ DATASOURCE_PROPERTIES + "]";

        InputStream propsInputStream = PersistenceUtil.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        Assert.assertNotNull(propertiesNotFound, propsInputStream);
        Properties props = new Properties();
        try {
            props.load(propsInputStream);
        } catch (IOException ioe) {
            Assert.fail(propertiesNotFound + ": " + ioe.getMessage());
            ioe.printStackTrace();
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
            for (String propertyName : new String[] { "serverName", "portNumber", "databaseName" }) {
                pds.getDriverProperties().put(propertyName, dsProps.getProperty(propertyName));
            }
        }

        return pds;
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
}
