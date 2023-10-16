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
package org.kie.kogito.trusty.storage.common;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.trusty.storage.api.model.ModelMetadata;
import org.kie.kogito.trusty.storage.api.model.ModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;

@ApplicationScoped
public class TrustyStorageServiceImpl implements TrustyStorageService {

    @Inject
    StorageService storageService;

    @Override
    public Storage<String, Decision> getDecisionsStorage() {
        return storageService.getCache(DECISIONS_STORAGE, Decision.class);
    }

    @Override
    public Storage<String, LIMEExplainabilityResult> getLIMEResultStorage() {
        return storageService.getCache(LIME_RESULTS_STORAGE, LIMEExplainabilityResult.class);
    }

    @Override
    public <T extends ModelMetadata, E extends ModelWithMetadata<T>> Storage<String, E> getModelStorage(Class<E> modelWithMetadata) {
        return storageService.getCache(MODELS_STORAGE, modelWithMetadata);
    }

    @Override
    public Storage<String, CounterfactualExplainabilityRequest> getCounterfactualRequestStorage() {
        return storageService.getCache(COUNTERFACTUAL_REQUESTS_STORAGE, CounterfactualExplainabilityRequest.class);
    }

    @Override
    public Storage<String, CounterfactualExplainabilityResult> getCounterfactualResultStorage() {
        return storageService.getCache(COUNTERFACTUAL_RESULTS_STORAGE, CounterfactualExplainabilityResult.class);
    }
}
