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
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplexEligibilityDmnCounterfactualExplainerTest {

    @Test
    void testDMNValidCounterfactualExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        final List<Output> goal = generateGoal(true, true, 0.6);

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("age", 40));
        constraints.add(true);
        featureBoundaries.add(EmptyFeatureDomain.create());
        features.add(FeatureFactory.newBooleanFeature("hasReferral", true));
        constraints.add(true);
        featureBoundaries.add(EmptyFeatureDomain.create());
        features.add(FeatureFactory.newNumericalFeature("monthlySalary", 500));
        featureBoundaries.add(NumericalFeatureDomain.create(10, 10_000));
        constraints.add(false);

        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(10_000L);
        // for the purpose of this test, only a few steps are necessary
        final SolverConfig solverConfig = SolverConfigBuilder
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed((long) 23);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);

        final CounterfactualConfig counterfactualConfig =
                new CounterfactualConfig().withSolverConfig(solverConfig).withGoalThreshold(0.01);
        final CounterfactualExplainer counterfactualExplainer =
                new CounterfactualExplainer(counterfactualConfig);

        PredictionInput input = new PredictionInput(features);

        PredictionOutput output = new PredictionOutput(goal);
        Prediction prediction = new CounterfactualPrediction(input,
                output,
                new PredictionFeatureDomain(featureBoundaries),
                constraints,
                null,
                UUID.randomUUID(), 60L);
        final CounterfactualResult counterfactualResult =
                counterfactualExplainer.explainAsync(prediction, model)
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        List<Output> cfOutputs = counterfactualResult.getOutput().get(0).getOutputs();

        assertTrue(counterfactualResult.isValid());
        assertEquals("inputsAreValid", cfOutputs.get(0).getName());
        assertTrue((Boolean) cfOutputs.get(0).getValue().getUnderlyingObject());
        assertEquals("canRequestLoan", cfOutputs.get(1).getName());
        assertTrue((Boolean) cfOutputs.get(1).getValue().getUnderlyingObject());
        assertEquals("my-scoring-function", cfOutputs.get(2).getName());
        assertEquals(0.6, ((BigDecimal) cfOutputs.get(2).getValue().getUnderlyingObject()).doubleValue(), 0.05);

        List<CounterfactualEntity> entities = counterfactualResult.getEntities();
        assertEquals("age", entities.get(0).asFeature().getName());
        assertEquals(entities.get(0).asFeature().getValue().asNumber(), 40);
        assertEquals("hasReferral", entities.get(1).asFeature().getName());
        assertTrue((Boolean) entities.get(1).asFeature().getValue().getUnderlyingObject());
        assertEquals("monthlySalary", entities.get(2).asFeature().getName());
        assertTrue(entities.get(2).asFeature().getValue().asNumber() > 6000);
    }

    @Test
    void testDMNScoringFunction() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        final List<Output> goal = generateGoal(true, true, 1.0);

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("age", 40));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(18, 60));
        features.add(FeatureFactory.newBooleanFeature("hasReferral", true));
        constraints.add(true);
        featureBoundaries.add(EmptyFeatureDomain.create());
        features.add(FeatureFactory.newNumericalFeature("monthlySalary", 500));
        featureBoundaries.add(NumericalFeatureDomain.create(10, 100_000));
        constraints.add(false);

        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(10_000L);
        // for the purpose of this test, only a few steps are necessary
        final SolverConfig solverConfig = SolverConfigBuilder
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed((long) 23);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);

        final CounterfactualConfig counterfactualConfig =
                new CounterfactualConfig().withSolverConfig(solverConfig).withGoalThreshold(0.01);
        final CounterfactualExplainer counterfactualExplainer =
                new CounterfactualExplainer(counterfactualConfig);

        PredictionInput input = new PredictionInput(features);

        PredictionOutput output = new PredictionOutput(goal);
        Prediction prediction = new CounterfactualPrediction(input,
                output,
                new PredictionFeatureDomain(featureBoundaries),
                constraints,
                null,
                UUID.randomUUID(), 60L);
        final CounterfactualResult counterfactualResult =
                counterfactualExplainer.explainAsync(prediction, model)
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        List<Output> cfOutputs = counterfactualResult.getOutput().get(0).getOutputs();

        assertTrue(counterfactualResult.isValid());
        assertEquals("inputsAreValid", cfOutputs.get(0).getName());
        assertTrue((Boolean) cfOutputs.get(0).getValue().getUnderlyingObject());
        assertEquals("canRequestLoan", cfOutputs.get(1).getName());
        assertTrue((Boolean) cfOutputs.get(1).getValue().getUnderlyingObject());
        assertEquals("my-scoring-function", cfOutputs.get(2).getName());
        assertEquals(1.0, ((BigDecimal) cfOutputs.get(2).getValue().getUnderlyingObject()).doubleValue(), 0.01);

        List<CounterfactualEntity> entities = counterfactualResult.getEntities();

        assertEquals("age", entities.get(0).asFeature().getName());
        assertEquals(18, entities.get(0).asFeature().getValue().asNumber());
        assertEquals("hasReferral", entities.get(1).asFeature().getName());
        assertTrue((Boolean) entities.get(1).asFeature().getValue().getUnderlyingObject());
        assertEquals("monthlySalary", entities.get(2).asFeature().getName());
        final double monthlySalary = entities.get(2).asFeature().getValue().asNumber();
        assertEquals(7900, monthlySalary, 10);

        // since the scoring function is ((0.6 * ((42 - age + 18)/42)) + (0.4 * (monthlySalary/8000)))
        // for a result of 1.0 the relation must be age = (7*monthlySalary)/2000 - 10
        assertEquals(18, (7 * monthlySalary) / 2000.0 - 10.0, 0.5);
    }

    @Test
    void testDMNInvalidCounterfactualExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        final List<Output> goal = generateGoal(true, true, 0.6);

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        // DMN model does not allow loans for age >= 60, so no CF will be possible
        features.add(FeatureFactory.newNumericalFeature("age", 61));
        constraints.add(true);
        featureBoundaries.add(EmptyFeatureDomain.create());
        features.add(FeatureFactory.newBooleanFeature("hasReferral", true));
        constraints.add(true);
        featureBoundaries.add(EmptyFeatureDomain.create());
        features.add(FeatureFactory.newNumericalFeature("monthlySalary", 500));
        featureBoundaries.add(NumericalFeatureDomain.create(10, 10_000));
        constraints.add(false);

        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(10_000L);
        // for the purpose of this test, only a few steps are necessary
        final SolverConfig solverConfig = SolverConfigBuilder
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed((long) 23);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);

        final CounterfactualConfig counterfactualConfig =
                new CounterfactualConfig().withSolverConfig(solverConfig).withGoalThreshold(0.01);
        final CounterfactualExplainer counterfactualExplainer =
                new CounterfactualExplainer(counterfactualConfig);

        PredictionInput input = new PredictionInput(features);

        PredictionOutput output = new PredictionOutput(goal);
        Prediction prediction = new CounterfactualPrediction(input,
                output,
                new PredictionFeatureDomain(featureBoundaries),
                constraints,
                null,
                UUID.randomUUID(), 60L);
        final CounterfactualResult counterfactualResult =
                counterfactualExplainer.explainAsync(prediction, model)
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        assertFalse(counterfactualResult.isValid());
    }

    private PredictionProvider getModel() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream(
                "/dmn/ComplexEligibility.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());
        final String COMPLEX_ELIGIBILITY_NS = "https://kiegroup.org/dmn/_B305FE71-3B8C-48C5-B5B1-D9CC04825B16";
        final String COMPLEX_ELIGIBILITY_NAME = "myComplexEligibility";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, COMPLEX_ELIGIBILITY_NS, COMPLEX_ELIGIBILITY_NAME);
        return new DecisionModelWrapper(decisionModel, List.of());
    }

    private List<Output> generateGoal(boolean inputsAreValid, boolean canRequestLoan, double scoringFunction) {
        return List.of(
                new Output("inputsAreValid", Type.BOOLEAN, new Value(inputsAreValid), 0.0),
                new Output("canRequestLoan", Type.BOOLEAN, new Value(canRequestLoan), 0.0),
                new Output("my-scoring-function", Type.NUMBER, new Value(scoringFunction), 0.0));
    }
}
