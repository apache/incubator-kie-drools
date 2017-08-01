/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
