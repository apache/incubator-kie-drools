package org.jbpm.persistence.scripts;

/**
 * Persistence units that are supported and used in tests.
 */
public enum PersistenceUnit {
    /**
     * Persistence unit used for SQL scripts execution.
     */
    SCRIPT_RUNNER("scriptRunner", "jdbc/testDS1"),

    /**
     * Persistence unit used for test cases validation.
     */
    DB_TESTING("dbTesting", "jdbc/testDS2"),

    /**
     * Persistence unit used for clearing the database schema.
     */
    CLEAR_SCHEMA("clearSchema", "jdbc/testDS3");

    /**
     * Name of persistence unit. Must correspond to persistence unit names in persistence.xml.
     */
    private final String name;

    /**
     * Name of data source bound to persistence unit. Must correspond to data source name in persistence.xml.
     */
    private final String dataSourceName;

    PersistenceUnit(final String name, final String dataSourceName) {
        this.name = name;
        this.dataSourceName = dataSourceName;
    }

    public String getName() {
        return name;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
