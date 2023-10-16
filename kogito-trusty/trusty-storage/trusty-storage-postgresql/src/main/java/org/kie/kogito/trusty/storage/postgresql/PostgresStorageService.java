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
package org.kie.kogito.trusty.storage.postgresql;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;

import io.quarkus.arc.AlternativePriority;
import io.quarkus.arc.properties.IfBuildProperty;

import static java.lang.String.format;
import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.postgresql.Constants.POSTGRESQL_STORAGE;

@ApplicationScoped
@AlternativePriority(1)
@IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = POSTGRESQL_STORAGE)
public class PostgresStorageService implements StorageService {

    private DecisionsStorage decisionsStorage;
    private LIMEResultsStorage limeResultsStorage;
    private DMNModelWithMetadataStorage dmnModelWithMetadataStorage;
    private CounterfactualRequestsStorage counterfactualRequestsStorage;
    private CounterfactualResultsStorage counterfactualResultsStorage;

    PostgresStorageService() {
        //CDI proxy
    }

    @Inject
    public PostgresStorageService(DecisionsStorage decisionsStorage, LIMEResultsStorage limeResultsStorage, DMNModelWithMetadataStorage dmnModelWithMetadataStorage,
            CounterfactualRequestsStorage counterfactualRequestsStorage, CounterfactualResultsStorage counterfactualResultsStorage) {
        this.decisionsStorage = decisionsStorage;
        this.limeResultsStorage = limeResultsStorage;
        this.dmnModelWithMetadataStorage = dmnModelWithMetadataStorage;
        this.counterfactualRequestsStorage = counterfactualRequestsStorage;
        this.counterfactualResultsStorage = counterfactualResultsStorage;
    }

    @Override
    public Storage<String, String> getCache(String name) {
        throw new UnsupportedOperationException("Generic String cache not available in PostgresSQL");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        if (type == Decision.class) {
            return (Storage<String, T>) decisionsStorage;
        } else if (type == LIMEExplainabilityResult.class) {
            return (Storage<String, T>) limeResultsStorage;
        } else if (type == DMNModelWithMetadata.class) {
            return (Storage<String, T>) dmnModelWithMetadataStorage;
        } else if (type == CounterfactualExplainabilityRequest.class) {
            return (Storage<String, T>) counterfactualRequestsStorage;
        } else if (type == CounterfactualExplainabilityResult.class) {
            return (Storage<String, T>) counterfactualResultsStorage;
        }
        throw new UnsupportedOperationException(format("Unknown class type: %s, cache not available", type.getCanonicalName()));
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type, String rootType) {
        throw new UnsupportedOperationException("Generic String cache not available in PostgresSQL");
    }
}
