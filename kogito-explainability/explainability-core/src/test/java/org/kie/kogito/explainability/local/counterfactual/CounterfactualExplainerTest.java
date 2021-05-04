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
package org.kie.kogito.explainability.local.counterfactual;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.DataDomain;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.IndependentFeaturesDataDistribution;
import org.kie.kogito.explainability.model.NumericFeatureDistribution;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.CategoricalFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.explainability.utils.DataUtils;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualExplainerTest {

    final long predictionTimeOut = 10L;
    final TimeUnit predictionTimeUnit = TimeUnit.MINUTES;
    final Long steps = 30_000L;

    private static final Logger logger =
            LoggerFactory.getLogger(CounterfactualExplainerTest.class);

    private CounterfactualResult runCounterfactualSearch(Long randomSeed, List<Output> goal,
            List<Boolean> constraints,
            DataDomain dataDomain,
            List<Feature> features,
            PredictionProvider model) throws InterruptedException, ExecutionException, TimeoutException {
        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(steps);
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed(randomSeed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        final CounterfactualExplainer explainer = CounterfactualExplainer
                .builder()
                .withSolverConfig(solverConfig)
                .build();
        final PredictionInput input = new PredictionInput(features);
        PredictionOutput output = new PredictionOutput(goal);
        PredictionFeatureDomain domain = new PredictionFeatureDomain(dataDomain.getFeatureDomains());
        Prediction prediction =
                new CounterfactualPrediction(input, output, domain, constraints, null, null, UUID.randomUUID());
        return explainer.explainAsync(prediction, model)
                .get(predictionTimeOut, predictionTimeUnit);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testNonEmptyInput(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);

        final List<Output> goal = List.of(new Output("class", Type.BOOLEAN, new Value(false), 0.0d));
        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
            featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
            constraints.add(false);
        }
        final DataDomain dataDomain = new DataDomain(featureBoundaries);
        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(10L);
        // for the purpose of this test, only a few steps are necessary
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed((long) seed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        final CounterfactualExplainer counterfactualExplainer =
                CounterfactualExplainer
                        .builder()
                        .withSolverConfig(solverConfig)
                        .build();

        PredictionProvider model = TestUtils.getSumSkipModel(0);

        PredictionInput input = new PredictionInput(features);
        PredictionOutput output = new PredictionOutput(goal);
        Prediction prediction =
                new CounterfactualPrediction(input, output, new PredictionFeatureDomain(featureBoundaries), constraints, null,
                        null, UUID.randomUUID());

        final CounterfactualResult counterfactualResult = counterfactualExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (CounterfactualEntity entity : counterfactualResult.getEntities()) {
            logger.debug("Entity: {}", entity);
        }

        logger.debug("Outputs: {}", counterfactualResult.getOutput().get(0).getOutputs());
        assertNotNull(counterfactualResult);
        assertNotNull(counterfactualResult.getEntities());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualMatch(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);

        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0d));
        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 150.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 1.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 2.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));

        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        TestUtils.getSumThresholdModel(center, epsilon));

        double totalSum = 0;
        for (CounterfactualEntity entity : result.getEntities()) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }

        logger.debug("Outputs: {}", result.getOutput().get(0).getOutputs());

        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualConstrainedMatchUnscaled(int seed)
            throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);

        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));

        // add a constraint
        constraints.set(0, true);
        constraints.set(3, true);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        TestUtils.getSumThresholdModel(center, epsilon));

        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();
        double totalSum = 0;
        for (CounterfactualEntity entity : counterfactualEntities) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }
        assertFalse(counterfactualEntities.get(0).isChanged());
        assertFalse(counterfactualEntities.get(3).isChanged());
        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualConstrainedMatchScaled(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0d));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        List<FeatureDistribution> featureDistributions = new LinkedList<>();

        final Feature fnum1 = FeatureFactory.newNumericalFeature("f-num1", 100.0);
        features.add(fnum1);
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        featureDistributions.add(new NumericFeatureDistribution(fnum1, (new NormalDistribution(500, 1.1)).sample(1000)));

        final Feature fnum2 = FeatureFactory.newNumericalFeature("f-num2", 100.0);
        features.add(fnum2);
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        featureDistributions.add(new NumericFeatureDistribution(fnum2, (new NormalDistribution(430.0, 1.7)).sample(1000)));

        final Feature fnum3 = FeatureFactory.newNumericalFeature("f-num3", 100.0);
        features.add(fnum3);
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        featureDistributions.add(new NumericFeatureDistribution(fnum3, (new NormalDistribution(470.0, 2.9)).sample(1000)));

        final Feature fnum4 = FeatureFactory.newNumericalFeature("f-num4", 100.0);
        features.add(fnum4);
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        featureDistributions.add(new NumericFeatureDistribution(fnum4, (new NormalDistribution(2390.0, 0.3)).sample(1000)));

        DataDistribution dataDistribution = new IndependentFeaturesDataDistribution(featureDistributions);
        // add a constraint
        constraints.set(0, true);
        constraints.set(3, true);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        TestUtils.getSumThresholdModel(center, epsilon));

        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();

        double totalSum = 0;
        for (CounterfactualEntity entity : counterfactualEntities) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }
        assertFalse(counterfactualEntities.get(0).isChanged());
        assertFalse(counterfactualEntities.get(3).isChanged());
        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualBoolean(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0d));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
            featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
            constraints.add(false);
        }
        features.add(FeatureFactory.newBooleanFeature("f-bool", true));
        featureBoundaries.add(null);
        constraints.add(false);
        // add a constraint
        constraints.set(2, true);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        TestUtils.getSumThresholdModel(center, epsilon));

        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();

        double totalSum = 0;
        for (CounterfactualEntity entity : counterfactualEntities) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }
        assertFalse(counterfactualEntities.get(2).isChanged());
        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualCategorical(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final List<Output> goal = List.of(new Output("result", Type.NUMBER, new Value(25.0), 0.0d));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("x-1", 5.0));
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 100.0));
        constraints.add(false);
        features.add(FeatureFactory.newNumericalFeature("x-2", 40.0));
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 100.0));
        constraints.add(false);
        features.add(FeatureFactory.newCategoricalFeature("operand", "*"));
        featureBoundaries.add(CategoricalFeatureDomain.create("+", "-", "/", "*"));
        constraints.add(false);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        TestUtils.getSymbolicArithmeticModel());

        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();

        Stream<Feature> counterfactualFeatures = counterfactualEntities
                .stream()
                .map(CounterfactualEntity::asFeature);
        String operand = counterfactualFeatures
                .filter(feature -> feature.getName().equals("operand"))
                .findFirst()
                .get()
                .getValue()
                .asString();

        List<Feature> numericalFeatures = counterfactualEntities
                .stream()
                .map(CounterfactualEntity::asFeature)
                .filter(feature -> !feature.getName().equals("operand"))
                .collect(Collectors.toList());

        double opResult = 0.0;
        for (Feature feature : numericalFeatures) {
            switch (operand) {
                case "+":
                    opResult += feature.getValue().asNumber();
                    break;
                case "-":
                    opResult -= feature.getValue().asNumber();
                    break;
                case "*":
                    opResult *= feature.getValue().asNumber();
                    break;
                case "/":
                    opResult /= feature.getValue().asNumber();
                    break;
            }
        }
        final double epsilon = 0.01;
        assertTrue(opResult <= 25.0 + epsilon);
        assertTrue(opResult >= 25.0 - epsilon);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualMatchThreshold(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final double scoreThreshold = 0.9;

        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), scoreThreshold));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));

        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final PredictionProvider model = TestUtils.getSumThresholdModel(center, epsilon);

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        model);

        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();

        double totalSum = 0;
        for (CounterfactualEntity entity : counterfactualEntities) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }
        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);

        final List<Feature> cfFeatures =
                counterfactualEntities.stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());
        final PredictionInput cfInput = new PredictionInput(cfFeatures);
        final PredictionOutput cfOutput = model.predictAsync(List.of(cfInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);

        final double predictionScore = cfOutput.getOutputs().get(0).getScore();
        logger.debug("Prediction score: {}", predictionScore);
        assertTrue(predictionScore >= scoreThreshold);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testCounterfactualMatchNoThreshold(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final double scoreThreshold = 0.0;

        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), scoreThreshold));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 100.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));

        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 10.0;

        final PredictionProvider model = TestUtils.getSumThresholdModel(center, epsilon);
        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, features,
                        model);
        final List<CounterfactualEntity> counterfactualEntities = result.getEntities();

        double totalSum = 0;
        for (CounterfactualEntity entity : counterfactualEntities) {
            totalSum += entity.asFeature().getValue().asNumber();
            logger.debug("Entity: {}", entity);
        }
        assertTrue(totalSum <= center + epsilon);
        assertTrue(totalSum >= center - epsilon);

        final List<Feature> cfFeatures =
                counterfactualEntities.stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());
        final PredictionInput cfInput = new PredictionInput(cfFeatures);
        final PredictionOutput cfOutput = model.predictAsync(List.of(cfInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);

        final double predictionScore = cfOutput.getOutputs().get(0).getScore();
        logger.debug("Prediction score: {}", predictionScore);
        assertTrue(predictionScore < 0.5);
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testNoCounterfactualPossible(int seed)
            throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);
        final PerturbationContext perturbationContext = new PerturbationContext(random, 4);
        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 1.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 2.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 1.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 2.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 1.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 2.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 1.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 2.0));

        // add a constraint
        constraints.set(0, true);
        constraints.set(3, true);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final double center = 500.0;
        final double epsilon = 1.0;

        List<Feature> perturbedFeatures = DataUtils.perturbFeatures(features, perturbationContext);

        final CounterfactualResult result =
                runCounterfactualSearch((long) seed, goal,
                        constraints,
                        dataDomain, perturbedFeatures,
                        TestUtils.getSumThresholdModel(center, epsilon));

        assertFalse(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testConsumers(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);

        final List<Output> goal = List.of(new Output("class", Type.BOOLEAN, new Value(false), 0.0d));
        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
            featureBoundaries.add(NumericalFeatureDomain.create(0.0, 1000.0));
            constraints.add(false);
        }
        final DataDomain dataDomain = new DataDomain(featureBoundaries);
        final TerminationConfig terminationConfig = new TerminationConfig().withScoreCalculationCountLimit(10L);
        // for the purpose of this test, only a few steps are necessary
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withTerminationConfig(terminationConfig).build();
        solverConfig.setRandomSeed((long) seed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);

        final Consumer<CounterfactualSolution> assertIntermediateCounterfactualNotNull = counterfactual -> {
        };

        final CounterfactualExplainer counterfactualExplainer =
                CounterfactualExplainer
                        .builder()
                        .withSolverConfig(solverConfig)
                        .build();

        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getSumSkipModel(0);
        PredictionOutput output = new PredictionOutput(goal);
        Prediction prediction = new CounterfactualPrediction(input, output, new PredictionFeatureDomain(featureBoundaries),
                constraints, null, assertIntermediateCounterfactualNotNull,
                UUID.randomUUID());
        final CounterfactualResult counterfactualResult = counterfactualExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (CounterfactualEntity entity : counterfactualResult.getEntities()) {
            logger.debug("Entity: {}", entity);
        }

        logger.debug("Outputs: {}", counterfactualResult.getOutput().get(0).getOutputs());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testIntermediateUniqueIds(int seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        random.setSeed(seed);

        final List<Output> goal = List.of(new Output("inside", Type.BOOLEAN, new Value(true), 0.0));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 10.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 10000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 10.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 10000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 10.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 10000.0));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 10.0));
        constraints.add(false);
        featureBoundaries.add(NumericalFeatureDomain.create(0.0, 10000.0));

        final double center = 400.0;
        final double epsilon = 1e-10;

        PredictionProvider model = TestUtils.getSumThresholdModel(center, epsilon);

        final TerminationConfig terminationConfig =
                new TerminationConfig().withBestScoreFeasible(true).withScoreCalculationCountLimit(10_000L);
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withTerminationConfig(terminationConfig).build();

        solverConfig.setRandomSeed((long) seed);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);

        final List<UUID> intermediateIds = new ArrayList<>();
        final List<UUID> executionIds = new ArrayList<>();

        final Consumer<CounterfactualSolution> captureIntermediateIds = counterfactual -> {
            intermediateIds.add(counterfactual.getSolutionId());
        };

        final Consumer<CounterfactualSolution> captureExecutionIds = counterfactual -> {
            executionIds.add(counterfactual.getExecutionId());
        };

        final CounterfactualExplainer counterfactualExplainer =
                CounterfactualExplainer
                        .builder()
                        .withSolverConfig(solverConfig)
                        .build();

        PredictionInput input = new PredictionInput(features);
        PredictionOutput output = new PredictionOutput(goal);
        final UUID executionId = UUID.randomUUID();
        Prediction prediction = new CounterfactualPrediction(input, output, new PredictionFeatureDomain(featureBoundaries),
                constraints, null, captureIntermediateIds.andThen(captureExecutionIds), executionId);
        final CounterfactualResult counterfactualResult = counterfactualExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        for (CounterfactualEntity entity : counterfactualResult.getEntities()) {
            logger.debug("Entity: {}", entity);
        }

        // all intermediate Ids must be distinct
        assertEquals((int) intermediateIds.stream().distinct().count(), intermediateIds.size());
        assertEquals((int) executionIds.stream().distinct().count(), 1);
        assertEquals(executionIds.get(0), executionId);
    }
}
