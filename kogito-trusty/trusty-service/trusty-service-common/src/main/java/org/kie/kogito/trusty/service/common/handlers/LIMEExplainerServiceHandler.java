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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LIMEExplainerServiceHandler extends BaseExplainerServiceHandler<LIMEExplainabilityResult, LIMEExplainabilityResultDto> {

    private static final Logger LOG = LoggerFactory.getLogger(LIMEExplainerServiceHandler.class);

    protected LIMEExplainerServiceHandler() {
        //CDI proxy
    }

    @Inject
    public LIMEExplainerServiceHandler(TrustyStorageService storageService) {
        super(storageService);
    }

    @Override
    public <T extends BaseExplainabilityResult> boolean supports(Class<T> type) {
        return LIMEExplainabilityResult.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BaseExplainabilityResultDto> boolean supportsDto(Class<T> type) {
        return LIMEExplainabilityResultDto.class.isAssignableFrom(type);
    }

    @Override
    public LIMEExplainabilityResult explainabilityResultFrom(LIMEExplainabilityResultDto dto, Decision decision) {
        Map<String, String> outcomeNameToIdMap = decision == null
                ? Collections.emptyMap()
                : decision.getOutcomes().stream().collect(Collectors.toUnmodifiableMap(DecisionOutcome::getOutcomeName, DecisionOutcome::getOutcomeId));

        List<SaliencyModel> saliencies = dto.getSaliencies() == null ? null
                : dto.getSaliencies().entrySet().stream()
                        .map(e -> saliencyFrom(outcomeNameToIdMap.get(e.getKey()), e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
        return new LIMEExplainabilityResult(dto.getExecutionId(),
                statusFrom(dto.getStatus()),
                dto.getStatusDetails(),
                saliencies);
    }

    private SaliencyModel saliencyFrom(String outcomeId, String outcomeName, SaliencyDto dto) {
        if (dto == null) {
            return null;
        }
        List<FeatureImportanceModel> featureImportanceModel = dto.getFeatureImportance() == null ? null
                : dto.getFeatureImportance().stream()
                        .map(this::featureImportanceFrom)
                        .collect(Collectors.toList());
        return new SaliencyModel(outcomeId, outcomeName, featureImportanceModel);
    }

    private FeatureImportanceModel featureImportanceFrom(FeatureImportanceDto dto) {
        if (dto == null) {
            return null;
        }
        return new FeatureImportanceModel(dto.getFeatureName(), dto.getScore());
    }

    @Override
    public LIMEExplainabilityResult getExplainabilityResultById(String executionId) {
        Storage<String, LIMEExplainabilityResult> storage = storageService.getLIMEResultStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("An explainability result with ID %s does not exist in the LIME results storage.", executionId));
        }
        return storage.get(executionId);
    }

    @Override
    public void storeExplainabilityResult(String executionId, LIMEExplainabilityResult result) {
        Storage<String, LIMEExplainabilityResult> storage = storageService.getLIMEResultStorage();
        if (storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("An explainability result with ID %s is already present in the LIME results storage.", executionId));
        }
        storage.put(executionId, result);
        LOG.info("Stored LIME explainability result for execution {}", executionId);
    }

}
