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
package org.kie.kogito.trusty.service.common.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.SaliencyModel;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.SalienciesResponse;
import org.kie.kogito.trusty.service.common.responses.SaliencyResponse;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;

/**
 * Converts from the generic {@link SaliencyModel} to {@link SaliencyResponse} comaptible with the needs of the UI.
 * In addition to "name" and "feature importance" the UI uses the original {@link DecisionOutcome} ID as a key to match
 * Salience with Outcome. The need for the specialised {@link SaliencyResponse} and this concerter class become
 * obsolete when https://issues.redhat.com/browse/FAI-655 is completed.
 */
@ApplicationScoped
public class LIMESaliencyConverter {

    protected TrustyService trustyService;

    protected LIMESaliencyConverter() {
        //CDI proxy
    }

    @Inject
    public LIMESaliencyConverter(TrustyService trustyService) {
        this.trustyService = trustyService;
    }

    public SalienciesResponse fromResult(String executionId,
            LIMEExplainabilityResult result) {
        return buildSalienciesResponse(trustyService.getDecisionById(executionId), result);
    }

    private SalienciesResponse buildSalienciesResponse(Decision decision,
            LIMEExplainabilityResult result) {
        Map<String, String> outcomeNameToIdMap = decision == null
                ? Collections.emptyMap()
                : decision.getOutcomes().stream().collect(Collectors.toUnmodifiableMap(DecisionOutcome::getOutcomeName, DecisionOutcome::getOutcomeId));

        List<SaliencyResponse> saliencies = result.getSaliencies() == null
                ? Collections.emptyList()
                : result.getSaliencies().stream()
                        .map(saliency -> saliencyFrom(outcomeNameToIdMap.get(saliency.getOutcomeName()), saliency))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        return new SalienciesResponse(result.getStatus().name(),
                result.getStatusDetails(),
                saliencies);
    }

    private SaliencyResponse saliencyFrom(String outcomeId,
            SaliencyModel saliency) {
        if (Objects.isNull(outcomeId)) {
            return null;
        }
        if (Objects.isNull(saliency)) {
            return null;
        }
        return new SaliencyResponse(outcomeId,
                saliency.getOutcomeName(),
                saliency.getFeatureImportance());
    }
}
