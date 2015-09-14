package org.jbpm.persistence.scripts.util;

import java.util.Properties;

/**
 * Contains util methods for working with SQL command.
 */
public final class SQLCommandUtil {

    /**
     * Preprocesses MS SQL Server SQL command. It modifies it so it can be executed without errors.
     * @param command Command that is preprocessed.
     * @param dataSourceProperties Properties of data source that is used to execute specified command.
     * @return Preprocessed SQL command.
     */
    public static String preprocessCommandSqlServer(final String command, final Properties dataSourceProperties) {
        return command.replace("enter_db_name_here", dataSourceProperties.getProperty("databaseName"));
    }

    private SQLCommandUtil() {
        // It makes no sense to create instances of util classes.
    }
}
