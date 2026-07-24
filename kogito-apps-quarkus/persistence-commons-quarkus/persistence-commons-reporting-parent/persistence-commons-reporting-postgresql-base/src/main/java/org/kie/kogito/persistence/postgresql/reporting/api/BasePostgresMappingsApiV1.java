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
package org.kie.kogito.persistence.postgresql.reporting.api;

import java.util.List;

import org.kie.kogito.persistence.postgresql.reporting.database.BasePostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresContext;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.postgresql.reporting.service.PostgresMappingServiceImpl;
import org.kie.kogito.persistence.reporting.api.BaseMappingsApiV1;

public abstract class BasePostgresMappingsApiV1
        extends BaseMappingsApiV1<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresMappingDefinition, PostgresMappingDefinitions, PostgresContext> {

    protected BasePostgresMappingsApiV1() {
        //CDI proxies
    }

    protected BasePostgresMappingsApiV1(final PostgresMappingServiceImpl mappingService,
            final BasePostgresDatabaseManagerImpl databaseManager) {
        super(mappingService, databaseManager);
    }

    @Override
    protected PostgresMappingDefinitions buildMappingDefinitions(final List<PostgresMappingDefinition> definitions) {
        return new PostgresMappingDefinitions(definitions);
    }
}
