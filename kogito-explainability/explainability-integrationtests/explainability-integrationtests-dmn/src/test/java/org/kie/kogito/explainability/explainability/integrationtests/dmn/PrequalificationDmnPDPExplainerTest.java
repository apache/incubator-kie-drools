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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.global.pdp.PartialDependencePlotExplainer;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.SimplePrediction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrequalificationDmnPDPExplainerTest {

    @Test
    void testPrequalificationDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/Prequalification-1.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String NS = "http://www.trisotech.com/definitions/_f31e1f8e-d4ce-4a3a-ac3b-747efa6b3401";
        final String NAME = "Prequalification";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, NS, NAME);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);

        List<PredictionInput> inputs = DmnTestUtils.randomPrequalificationInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(inputs)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        List<Prediction> predictions = new ArrayList<>();
        for (int i = 0; i < predictionOutputs.size(); i++) {
            predictions.add(new SimplePrediction(inputs.get(i), predictionOutputs.get(i)));
        }

        PartialDependencePlotExplainer partialDependencePlotExplainer = new PartialDependencePlotExplainer();
        List<PartialDependenceGraph> pdps = partialDependencePlotExplainer.explainFromPredictions(model, predictions);

        AssertionsForClassTypes.assertThat(pdps).isNotNull();
        Assertions.assertThat(pdps).hasSize(25);
    }

}
