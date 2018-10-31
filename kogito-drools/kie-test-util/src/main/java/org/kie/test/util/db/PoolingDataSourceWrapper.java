package org.kie.test.util.db;

import javax.sql.DataSource;

/**
 * Wrapper for an XA data source with pooling capabilities.
 */
public interface PoolingDataSourceWrapper extends DataSource {

    /**
     * Closes the data source; as a result, the data source will stop providing connections and will be unregistered
     * from JNDI context.
     */
    void close();

    /**
     * @return the data source JNDI name
     */
    String getUniqueName();

    /**
     * @return name of underlying XADataSource class
     */
    String getClassName();
}
