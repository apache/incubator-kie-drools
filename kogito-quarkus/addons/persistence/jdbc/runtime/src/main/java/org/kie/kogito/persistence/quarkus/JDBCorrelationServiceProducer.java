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
package org.kie.kogito.persistence.quarkus;

import java.util.Optional;

import javax.sql.DataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.correlation.CorrelationService;
import org.kie.kogito.event.correlation.DefaultCorrelationService;
import org.kie.kogito.persistence.jdbc.correlation.PostgreSQLCorrelationService;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import static org.kie.kogito.persistence.quarkus.KogitoAddOnPersistenceJDBCConfigSourceFactory.DATASOURCE_DB_KIND;
import static org.kie.kogito.persistence.quarkus.KogitoAddOnPersistenceJDBCConfigSourceFactory.POSTGRESQL;

public class JDBCorrelationServiceProducer {

    @Inject
    @ConfigProperty(name = DATASOURCE_DB_KIND)
    Optional<String> dbKind;

    @Produces
    public CorrelationService jdbcCorrelationService(DataSource dataSource) {
        return dbKind.filter(POSTGRESQL::equals).isPresent() ? new PostgreSQLCorrelationService(dataSource) : new DefaultCorrelationService();
    }
}
