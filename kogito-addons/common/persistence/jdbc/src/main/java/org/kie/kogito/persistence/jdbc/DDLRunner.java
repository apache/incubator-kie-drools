/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.persistence.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDLRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDLRunner.class);

    public static void init(Repository repository, boolean autoDDL) {
        if (!autoDDL) {
            LOGGER.debug("Auto DDL is disabled, do not running initializer scripts");
            return;
        }
        try {
            if (!repository.tableExists()) {
                LOGGER.info("Dynamically creating process_instances table");
                repository.createTable();
            }
        } catch (Exception e) {
            // not break the execution flow in case of any missing permission for db application user, for instance.
            LOGGER.error(e.getMessage(), e);
        }
    }
}
