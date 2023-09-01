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

import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp2.managed.BasicManagedDataSource;
import org.kie.test.util.db.internal.DatabaseProvider;
import org.kie.test.util.db.internal.PoolingDataSourceWrapperImpl;

/**
 * Creates instances of {@link PoolingDataSourceWrapper} that can be used for testing purposes.
 */
public final class DataSourceFactory {

    private DataSourceFactory() {
        throw new UnsupportedOperationException(DataSourceFactory.class.getSimpleName() + " should not be instantiated.");
    }

    /**
     * Creates a new instance of {@link PoolingDataSourceWrapper}.
     * @param datasourceName data source JNDI name
     * @param driverProperties properties that should be passed to JDBC driver
     * @return a new PoolingDataSourceWrapper instance
     */
    public static PoolingDataSourceWrapper setupPoolingDataSource(String datasourceName,
                                                                      Properties driverProperties) {
        return setupPoolingDataSource(datasourceName, driverProperties, new Properties());
    }

    /**
     * Creates a new instance of {@link PoolingDataSourceWrapper}.
     * @param datasourceName data source JNDI name
     * @param driverProperties properties that should be passed to JDBC driver
     * @param poolingProperties properties of {@link BasicManagedDataSource} pooling data source
     * @return a new PoolingDataSourceWrapper instance
     */
    public static PoolingDataSourceWrapper setupPoolingDataSource(String datasourceName,
                                                                      Properties driverProperties,
                                                                      Properties poolingProperties) {
        Properties sanitizedDriverProperties = new Properties();
        String driverClass = driverProperties.getProperty("driverClassName");
        DatabaseProvider databaseProvider = DatabaseProvider.fromDriverClassName(driverClass);
        for (String propertyName : new String[]{"user", "password"}) {
            sanitizedDriverProperties.put(propertyName, driverProperties.getProperty(propertyName));
        }

        if (databaseProvider == DatabaseProvider.H2) {
            for (String propertyName : new String[]{"url", "driverClassName"}) {
                sanitizedDriverProperties.put(propertyName, driverProperties.getProperty(propertyName));
            }
        } else {
            if (databaseProvider == DatabaseProvider.ORACLE) {
                sanitizedDriverProperties.put("driverType", "thin");
                sanitizedDriverProperties.put("URL", driverProperties.getProperty("url"));
            } else if (databaseProvider == DatabaseProvider.DB2) {
                // http://docs.codehaus.org/display/BTM/JdbcXaSupportEvaluation#JdbcXaSupportEvaluation-IBMDB2
                for (String propertyName : new String[]{"databaseName", "serverName", "portNumber", "url"}) {
                    sanitizedDriverProperties.put(propertyName, driverProperties.getProperty(propertyName));
                }
                sanitizedDriverProperties.put("driverType", "4");
                sanitizedDriverProperties.put("currentSchema", driverProperties.getProperty("defaultSchema"));
                sanitizedDriverProperties.put("ResultSetHoldability", "1");
                sanitizedDriverProperties.put("DowngradeHoldCursorsUnderXa", "true");
            } else if (databaseProvider == DatabaseProvider.MSSQL) {
                for (String propertyName : new String[]{"serverName", "portNumber", "databaseName"}) {
                    sanitizedDriverProperties.put(propertyName, driverProperties.getProperty(propertyName));
                }
                sanitizedDriverProperties.put("URL", driverProperties.getProperty("url"));
            } else if (databaseProvider == DatabaseProvider.MYSQL
                    || databaseProvider == DatabaseProvider.MARIADB
                    || databaseProvider == DatabaseProvider.SYBASE
                    || databaseProvider == DatabaseProvider.POSTGRES
                    || databaseProvider == DatabaseProvider.POSTGRES_PLUS) {
                for (String propertyName : new String[]{"databaseName", "portNumber", "serverName", "url"}) {
                    sanitizedDriverProperties.put(propertyName, driverProperties.getProperty(propertyName));
                }
            } else {
                throw new RuntimeException("Unknown driver class: " + driverClass);
            }
        }

        String xaDataSourceClassName = driverProperties.getProperty("className");
        return new PoolingDataSourceWrapperImpl(datasourceName, xaDataSourceClassName, sanitizedDriverProperties, poolingProperties);
    }
}
