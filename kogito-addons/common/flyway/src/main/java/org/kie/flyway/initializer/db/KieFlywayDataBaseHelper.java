/*
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

package org.kie.flyway.initializer.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.kie.flyway.KieFlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieFlywayDataBaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(KieFlywayDataBaseHelper.class);

    private KieFlywayDataBaseHelper() {
    }

    public static DataBaseInfo readDataBaseInfo(DataSource ds) {
        try (Connection con = ds.getConnection()) {

            DatabaseMetaData metadata = con.getMetaData();

            String name = metadata.getDatabaseProductName();
            String version = metadata.getDatabaseProductVersion();

            LOGGER.info("Reading DataBase Product: '{}' Version: '{}'", name, version);

            return new DataBaseInfo(name, version);
        } catch (Exception e) {
            LOGGER.error("Kie Flyway: Couldn't extract database product name from datasource ", e);
            throw new KieFlywayException("Kie Flyway: Couldn't extract database product name from datasource.", e);
        }
    }
}
