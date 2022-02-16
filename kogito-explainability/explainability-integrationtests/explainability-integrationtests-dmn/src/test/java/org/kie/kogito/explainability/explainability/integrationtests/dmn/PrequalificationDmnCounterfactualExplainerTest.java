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
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualConfig;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.local.counterfactual.SolverConfigBuilder;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.explainability.utils.CompositeFeatureUtils;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrequalificationDmnCounterfactualExplainerTest {

    private static final long steps = 100_000;
    private static final long randomSeed = 23;

    @Test
    void testValidCounterfactual() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        final List<Output> goal = List.of(
                new Output("Qualified?", Type.BOOLEAN, new Value(true), 0.0d));

        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(steps);
        final SolverConfig solverConfig = SolverConfigBuilder
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed(randomSeed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        CounterfactualConfig config = new CounterfactualConfig().withGoalThreshold(0.1);
        config.withSolverConfig(solverConfig);
        final CounterfactualExplainer explainer = new CounterfactualExplainer(config);

        PredictionInput input = getTestInputVariable();
        PredictionOutput output = new PredictionOutput(goal);

        // test model
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(getTestInputFixed()))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        final Output predictionOutput = predictionOutputs.get(0).getOutputs().get(0);
        assertEquals("Qualified?", predictionOutput.getName());
        assertFalse((Boolean) predictionOutput.getValue().getUnderlyingObject());

        Prediction prediction =
                new CounterfactualPrediction(input, output, null, UUID.randomUUID(), null);
        CounterfactualResult counterfactualResult = explainer.explainAsync(prediction, model).get();

        List<Feature> cfFeatures = counterfactualResult.getEntities().stream().map(CounterfactualEntity::asFeature).collect(
                Collectors.toList());
        List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(cfFeatures, input.getFeatures());

        List<PredictionOutput> outputs = model.predictAsync(List.of(new PredictionInput(unflattened))).get();

        assertTrue(counterfactualResult.isValid());
        final Output decideOutput = outputs.get(0).getOutputs().get(0);
        assertEquals("Qualified?", decideOutput.getName());
        assertTrue((Boolean) decideOutput.getValue().getUnderlyingObject());
    }

    private PredictionInput getTestInputFixed() {
        final Map<String, Object> borrower = new HashMap<>();
        borrower.put("Monthly Other Debt", 5000);
        borrower.put("Monthly Income", 10_000);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Appraised Value", 500000);
        contextVariables.put("Loan Amount", 500_000);
        contextVariables.put("Credit Score", 700);
        contextVariables.put("Best Rate", 1);
        contextVariables.put("Borrower", borrower);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        return new PredictionInput(features);
    }

    private PredictionInput getTestInputVariable() {
        final Map<String, Object> borrower = new HashMap<>();
        borrower.put("Monthly Other Debt",
                FeatureFactory.newNumericalFeature("Monthly Other Debt", 10_000,
                        NumericalFeatureDomain.create(0.0, 10_000.0)));
        borrower.put("Monthly Income", FeatureFactory.newNumericalFeature("Monthly Income", 10_000,
                NumericalFeatureDomain.create(1000, 500_000)));
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Appraised Value", 500000);
        contextVariables.put("Loan Amount",
                FeatureFactory.newNumericalFeature("Loan Amount", 500_000,
                        NumericalFeatureDomain.create(10.0, 500_000.0)));
        contextVariables.put("Credit Score", 700);
        contextVariables.put("Best Rate", 1);
        contextVariables.put("Borrower", borrower);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        return new PredictionInput(features);
    }

    private PredictionProvider getModel() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/Prequalification-1.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String NS = "http://www.trisotech.com/definitions/_f31e1f8e-d4ce-4a3a-ac3b-747efa6b3401";
        final String NAME = "Prequalification";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, NS, NAME);
        return new DecisionModelWrapper(decisionModel, List.of("LTV", "LLPA", "DTI", "Loan Payment"));
    }
}