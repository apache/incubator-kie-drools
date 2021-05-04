/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service.common.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CounterfactualExplainerServiceHandler extends BaseExplainerServiceHandler<CounterfactualExplainabilityResult, CounterfactualExplainabilityResultDto> {

    private static final Logger LOG = LoggerFactory.getLogger(CounterfactualExplainerServiceHandler.class);

    protected CounterfactualExplainerServiceHandler() {
        //CDI proxy
    }

    @Inject
    public CounterfactualExplainerServiceHandler(TrustyStorageService storageService) {
        super(storageService);
    }

    @Override
    public <T extends BaseExplainabilityResult> boolean supports(Class<T> type) {
        return CounterfactualExplainabilityResult.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BaseExplainabilityResultDto> boolean supportsDto(Class<T> type) {
        return CounterfactualExplainabilityResultDto.class.isAssignableFrom(type);
    }

    @Override
    public CounterfactualExplainabilityResult explainabilityResultFrom(CounterfactualExplainabilityResultDto dto, Decision decision) {
        return new CounterfactualExplainabilityResult(dto.getExecutionId(),
                dto.getCounterfactualId(),
                "solutionId",
                statusFrom(dto.getStatus()),
                dto.getStatusDetails());
    }

    @Override
    public CounterfactualExplainabilityResult getExplainabilityResultById(String executionId) {
        Storage<String, CounterfactualExplainabilityResult> storage = storageService.getCounterfactualResultStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("An explainability result with ID %s does not exist in the Counterfactual results storage.", executionId));
        }
        return storage.get(executionId);
    }

    @Override
    public void storeExplainabilityResult(String executionId, CounterfactualExplainabilityResult result) {
        Storage<String, CounterfactualExplainabilityResult> storage = storageService.getCounterfactualResultStorage();
        if (storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("An explainability result with ID %s is already present in the Counterfactual results storage.", executionId));
        }
        storage.put(executionId, result);
        LOG.info("Stored explainability result for execution {}", executionId);
    }
}
