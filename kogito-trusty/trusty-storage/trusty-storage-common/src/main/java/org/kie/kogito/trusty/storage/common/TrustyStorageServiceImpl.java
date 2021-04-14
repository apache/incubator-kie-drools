/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.storage.common;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualResult;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.Decision;

@ApplicationScoped
public class TrustyStorageServiceImpl implements TrustyStorageService {

    @Inject
    StorageService storageService;

    @Override
    public Storage<String, Decision> getDecisionsStorage() {
        return storageService.getCache(DECISIONS_STORAGE, Decision.class);
    }

    @Override
    public Storage<String, BaseExplainabilityResult> getExplainabilityResultStorage() {
        return storageService.getCache(EXPLAINABILITY_RESULTS_STORAGE, BaseExplainabilityResult.class);
    }

    @Override
    public Storage<String, DMNModelWithMetadata> getModelStorage() {
        return storageService.getCache(MODELS_STORAGE, DMNModelWithMetadata.class);
    }

    @Override
    public Storage<String, CounterfactualRequest> getCounterfactualRequestStorage() {
        return storageService.getCache(COUNTERFACTUALS_STORAGE, CounterfactualRequest.class);
    }

    @Override
    public Storage<String, CounterfactualResult> getCounterfactualResultStorage() {
        return storageService.getCache(COUNTERFACTUAL_RESULTS_STORAGE, CounterfactualResult.class);
    }
}
