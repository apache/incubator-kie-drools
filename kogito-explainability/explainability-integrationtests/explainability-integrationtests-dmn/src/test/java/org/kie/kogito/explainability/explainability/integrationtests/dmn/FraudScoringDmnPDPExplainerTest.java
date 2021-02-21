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
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.global.pdp.PartialDependencePlotExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.utils.DataUtils;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FraudScoringDmnPDPExplainerTest {

    @Test
    void testFraudScoringDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/fraud.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String FRAUD_NS = "http://www.redhat.com/dmn/definitions/_81556584-7d78-4f8c-9d5f-b3cddb9b5c73";
        final String FRAUD_NAME = "fraud-scoring";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, FRAUD_NS, FRAUD_NAME);
        PredictionProvider model = new DecisionModelWrapper(decisionModel);


        List<PredictionInput> inputs = getInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(inputs)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        List<Prediction> predictions = new ArrayList<>();
        for (int i = 0; i < predictionOutputs.size(); i++) {
            predictions.add(new Prediction(inputs.get(i), predictionOutputs.get(i)));
        }

        PartialDependencePlotExplainer partialDependencePlotExplainer = new PartialDependencePlotExplainer();
        List<PartialDependenceGraph> pdps = partialDependencePlotExplainer.explainFromPredictions(model, predictions);

        assertThat(pdps).isNotNull();
        Assertions.assertThat(pdps).hasSize(32);

    }

    private List<PredictionInput> getInputs() {
        List<PredictionInput> predictionInputs = new ArrayList<>();
        List<Map<String, Object>> transactions = new ArrayList<>();
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
        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", map));
        PredictionInput predictionInput = new PredictionInput(features);
        predictionInputs.add(predictionInput);

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            List<Feature> perturbFeatures = DataUtils.perturbFeatures(predictionInput.getFeatures(), new PerturbationContext(random, predictionInput.getFeatures().size()));
            predictionInputs.add(new PredictionInput(perturbFeatures));
        }

        return predictionInputs;
    }
}
