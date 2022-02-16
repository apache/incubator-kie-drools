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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanEligibilityDmnCounterfactualExplainerTest {

    private static final long steps = 100_000;
    private static final long randomSeed = 23;

    @Test
    void testLoanEligibilityDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        final List<Output> goal = List.of(
                new Output("Is Enought?", Type.NUMBER, new Value(100), 0.0d),
                new Output("Eligibility", Type.TEXT, new Value("No"), 0.0d),
                new Output("Decide", Type.BOOLEAN, new Value(true), 0.0d));

        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(steps);
        final SolverConfig solverConfig = SolverConfigBuilder
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed(randomSeed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        CounterfactualConfig config = new CounterfactualConfig();
        config.withSolverConfig(solverConfig);
        final CounterfactualExplainer explainer = new CounterfactualExplainer(config);

        PredictionInput input = getTestInput();
        PredictionOutput output = new PredictionOutput(goal);

        // test model
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        Prediction prediction =
                new CounterfactualPrediction(input, output, null, UUID.randomUUID(), null);
        CounterfactualResult counterfactualResult = explainer.explainAsync(prediction, model).get();

        List<Feature> cfFeatures = counterfactualResult.getEntities().stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());
        List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(cfFeatures, input.getFeatures());

        List<PredictionOutput> outputs = model.predictAsync(List.of(new PredictionInput(unflattened))).get();

        assertTrue(counterfactualResult.isValid());
        final Output decideOutput = outputs.get(0).getOutputs().get(2);
        assertEquals("Decide", decideOutput.getName());
        assertTrue((Boolean) decideOutput.getValue().getUnderlyingObject());
    }

    private PredictionProvider getModel() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/dmn/LoanEligibility.dmn"))));
        assertEquals(1, dmnRuntime.getModels().size());

        final String FRAUD_NS = "https://github.com/kiegroup/kogito-examples/dmn-quarkus-listener-example";
        final String FRAUD_NAME = "LoanEligibility";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, FRAUD_NS, FRAUD_NAME);
        return new DecisionModelWrapper(decisionModel, List.of("Judgement"));
    }

    private PredictionInput getTestInput() {
        final Map<String, Object> client = new HashMap<>();
        client.put("Age", 43);
        client.put("Salary",
                FeatureFactory.newNumericalFeature("Salary", 100,
                        NumericalFeatureDomain.create(0.0, 1000.0)));
        client.put("Existing payments",
                FeatureFactory.newNumericalFeature("Existing payments", 100,
                        NumericalFeatureDomain.create(0, 1000)));
        final Map<String, Object> loan = new HashMap<>();
        loan.put("Duration",
                FeatureFactory.newNumericalFeature("Duration", 15,
                        NumericalFeatureDomain.create(0, 1000)));
        loan.put("Installment", 100);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Client", client);
        contextVariables.put("Loan", loan);
        contextVariables.put("God", FeatureFactory.newCategoricalFeature("God", "No"));
        contextVariables.put("Bribe", FeatureFactory.newNumericalFeature("Bribe", 0.0, NumericalFeatureDomain.create(0.0, 1000.0)));

        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        return new PredictionInput(features);
    }
}