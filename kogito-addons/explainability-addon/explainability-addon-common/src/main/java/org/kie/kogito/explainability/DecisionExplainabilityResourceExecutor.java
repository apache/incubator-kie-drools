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
package org.kie.kogito.explainability;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.Application;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.explainability.model.ModelIdentifier;
import org.kie.kogito.explainability.model.PredictInput;
import org.kie.kogito.explainability.model.PredictOutput;

import static org.kie.kogito.explainability.Constants.SKIP_MONITORING;
import static org.kie.kogito.explainability.Constants.SKIP_TRACING;
import static org.kie.kogito.explainability.model.ModelIdentifier.RESOURCE_ID_SEPARATOR;

public class DecisionExplainabilityResourceExecutor implements ExplainabilityResourceExecutor {

    @Override
    public boolean acceptRequest(PredictInput predictInput) {
        return "dmn".equalsIgnoreCase(predictInput.getModelIdentifier().getResourceType());
    }

    @Override
    public PredictOutput processRequest(Application application, PredictInput predictInput) {
        DecisionModel decisionModel = getDecisionModel(application.get(DecisionModels.class), predictInput.getModelIdentifier());
        DMNContext dmnContext = decisionModel.newContext(convertDMNInput(predictInput));
        dmnContext.getMetadata().set(SKIP_TRACING, true);
        dmnContext.getMetadata().set(SKIP_MONITORING, true);
        return convertDMNOutput(decisionModel.evaluateAll(dmnContext), predictInput);
    }

    protected DecisionModel getDecisionModel(DecisionModels decisionModels, ModelIdentifier modelIdentifier) {
        String[] namespaceAndName = extractNamespaceAndName(modelIdentifier.getResourceId());
        return decisionModels.getDecisionModel(namespaceAndName[0], namespaceAndName[1]);
    }

    private Map<String, Object> convertDMNInput(PredictInput predictInput) {
        return predictInput.getRequest();
    }

    private PredictOutput convertDMNOutput(DMNResult dmnResult, PredictInput predictInput) {
        String[] namespaceAndName = extractNamespaceAndName(predictInput.getModelIdentifier().getResourceId());
        KogitoDMNResult result = new KogitoDMNResult(
                namespaceAndName[0],
                namespaceAndName[1],
                dmnResult);
        return new PredictOutput(predictInput.getModelIdentifier(), result.getDmnContext());
    }

    private String[] extractNamespaceAndName(String resourceId) {
        int index = resourceId.lastIndexOf(RESOURCE_ID_SEPARATOR);
        if (index < 0 || index == resourceId.length()) {
            throw new IllegalArgumentException("Malformed resourceId " + resourceId);
        }
        return new String[] { resourceId.substring(0, index), resourceId.substring(index + 1) };
    }
}
