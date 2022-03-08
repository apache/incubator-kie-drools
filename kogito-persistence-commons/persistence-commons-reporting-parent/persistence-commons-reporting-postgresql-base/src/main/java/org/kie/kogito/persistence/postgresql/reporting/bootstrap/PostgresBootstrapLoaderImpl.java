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
package org.kie.kogito.persistence.postgresql.reporting.bootstrap;

import java.io.InputStream;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.bootstrap.BaseBootstrapLoaderImpl;

@ApplicationScoped
public class PostgresBootstrapLoaderImpl
        extends BaseBootstrapLoaderImpl<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresMappingDefinition, PostgresMappingDefinitions> {

    PostgresBootstrapLoaderImpl() {
        //CDI proxy
    }

    //For unit testing
    PostgresBootstrapLoaderImpl(final Supplier<InputStream> inputStreamSupplier) {
        super(inputStreamSupplier);
    }

    @Override
    public Class<PostgresMappingDefinitions> getMappingDefinitionsType() {
        return PostgresMappingDefinitions.class;
    }
}
