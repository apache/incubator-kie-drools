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
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * {@link PredictionProvider} implementation based on a Kogito {@link DecisionModel}.
 */
class DecisionModelWrapper implements PredictionProvider {

    private final DecisionModel decisionModel;
    private final List<String> skippedDecisions;

    DecisionModelWrapper(DecisionModel decisionModel) {
        this(decisionModel, Collections.emptyList());
    }

    DecisionModelWrapper(DecisionModel decisionModel, List<String> skippedDecisions) {
        this.decisionModel = decisionModel;
        this.skippedDecisions = skippedDecisions;
    }

    @Override
    public CompletableFuture<List<PredictionOutput>> predictAsync(List<PredictionInput> inputs) {
        List<PredictionOutput> predictionOutputs = new LinkedList<>();
        for (PredictionInput input : inputs) {
            Map<String, Object> contextVariables = toMap(input.getFeatures());
            final DMNContext context = decisionModel.newContext(contextVariables);
            DMNResult dmnResult = decisionModel.evaluateAll(context);
            List<Output> outputs = new LinkedList<>();
            for (DMNDecisionResult decisionResult : dmnResult.getDecisionResults()) {
                String decisionName = decisionResult.getDecisionName();
                if (!skippedDecisions.contains(decisionName)) {
                    Value value = new Value(decisionResult.getResult());
                    Type type;
                    if (Double.isNaN(value.asNumber())) {
                        type = Type.TEXT;
                    } else {
                        type = Type.NUMBER;
                    }
                    Output output = new Output(decisionName, type, value, 1d);
                    outputs.add(output);
                }
            }
            PredictionOutput predictionOutput = new PredictionOutput(outputs);
            predictionOutputs.add(predictionOutput);
        }
        return completedFuture(predictionOutputs);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(List<Feature> features) {
        Map<String, Object> map = new HashMap<>();
        for (Feature f : features) {
            if (Type.COMPOSITE.equals(f.getType())) {
                List<Feature> compositeFeatures = (List<Feature>) f.getValue().getUnderlyingObject();
                boolean isList = compositeFeatures.stream().allMatch(feature -> feature.getName().startsWith(f.getName() + "_"));
                if (isList) {
                    List<Object> objects = new ArrayList<>(compositeFeatures.size());
                    for (Feature fs : compositeFeatures) {
                        try {
                            objects.add(toMap((List<Feature>) fs.getValue().getUnderlyingObject()));
                        } catch (ClassCastException cce) {
                            objects.add(fs.getValue().getUnderlyingObject());
                        }
                    }
                    map.put(f.getName(), objects);
                } else {
                    Map<String, Object> maps = new HashMap<>();
                    for (Feature cf : compositeFeatures) {
                        Map<String, Object> compositeFeatureMap = toMap(List.of(cf));
                        maps.putAll(compositeFeatureMap);
                    }
                    map.put(f.getName(), maps);
                }
            } else {
                if (Type.UNDEFINED.equals(f.getType())) {
                    Feature underlyingFeature = (Feature) f.getValue().getUnderlyingObject();
                    map.put(f.getName(), toMap(List.of(underlyingFeature)));
                } else {
                    Object underlyingObject = f.getValue().getUnderlyingObject();
                    map.put(f.getName(), underlyingObject);
                }
            }
        }
        if (map.containsKey("context")) {
            map = (Map<String, Object>) map.get("context");
        }
        return map;
    }

}
