package org.jbpm.persistence.scripts;

/**
 * Represents various supported database types. Based on supported hibernate dialects.
 */
public enum DatabaseType {

    DB2("db2"),
    DERBY("derby"),
    H2("h2"),
    HSQLDB("hsqldb"),
    MYSQL5("mysql5"),
    MYSQLINNODB("mysqlinnodb"),
    ORACLE("oracle"),
    POSTGRESQL("postgresql"),
    SQLSERVER("sqlserver"),
    SQLSERVER2008("sqlserver2008");

    private String scriptsFolderName;

    /**
     * Constructor.
     *
     * @param scriptsFolderName Name of folder which contains scripts for database type.
     */
    DatabaseType(String scriptsFolderName) {
        this.scriptsFolderName = scriptsFolderName;
    }

    /**
     * Gets name of folder which contains scripts for database type.
     *
     * @return Name of folder which contains scripts for database type.
     */
    public String getScriptsFolderName() {
        return scriptsFolderName;
    }
}
