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

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FraudScoringDmnLimeExplainerTest {

    @Test
    void testFraudScoringDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/fraud.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String FRAUD_NS = "http://www.redhat.com/dmn/definitions/_81556584-7d78-4f8c-9d5f-b3cddb9b5c73";
        final String FRAUD_NAME = "fraud-scoring";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, FRAUD_NS, FRAUD_NAME);
        List<Map<String, Object>> transactions = new LinkedList<>();
        Map<String, Object> t1 = new HashMap<>();
        t1.put("Card Type", "Debit");
        t1.put("Location", "Local");
        t1.put("Amount", 1000);
        t1.put("Auth Code", "Authorized");
        transactions.add(t1);
        Map<String, Object> t2 = new HashMap<>();
        t2.put("Card Type", "Credit");
        t2.put("Location", "Local");
        t2.put("Amount", 100000);
        t2.put("Auth Code", "Denied");
        transactions.add(t2);
        Map<String, Object> map = new HashMap<>();
        map.put("Transactions", transactions);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", map));
        PredictionInput predictionInput = new PredictionInput(features);
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(predictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new Prediction(predictionInput, predictionOutputs.get(0));
        Random random = new Random();
        random.setSeed(4);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(100)
                .withPerturbationContext(new PerturbationContext(random, 5));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(4);
            double topScore = Math.abs(topFeatures.stream().map(FeatureImportance::getScore).findFirst().orElse(0d));
            if (!topFeatures.isEmpty() && topScore > 0) {
                double v = ExplainabilityMetrics.impactScore(model, prediction, topFeatures);
                assertThat(v).isPositive(); // checks the drop of important features triggers a flipped prediction (or a significant drop in the output score).
            }
        }
    }
}
