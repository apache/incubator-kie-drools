package org.drools.ruleunits.dsl.util;

import org.drools.ruleunits.api.DataSource;

public class DataSourceDefinition {
    private final DataSource dataSource;
    private final Class<?> dataClass;

    public DataSourceDefinition(DataSource dataSource, Class<?> dataClass) {
        this.dataSource = dataSource;
        this.dataClass = dataClass;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }
}