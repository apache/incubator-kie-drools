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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualScoreCalculatorTest {

    private static Output outputFromFeature(Feature feature) {
        return new Output(feature.getName(), feature.getType(), feature.getValue(), 0d);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void IntegerDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final int value = random.nextInt();
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        // Use a random threshold, mustn't make a difference
        final double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(0.0, Math.abs(distance));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void IntegerDistanceSameValueZero(int seed) {
        final Random random = new Random(seed);
        final int value = 0;
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        // Use a random threshold, mustn't make a difference
        final double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(0.0, Math.abs(distance));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void DoubleDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final double value = random.nextDouble();
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        // Use a random threshold, mustn't make a difference
        final double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void DoubleDistanceSameValueZero(int seed) {
        final Random random = new Random(seed);
        final double value = 0.0;
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        // Use a random threshold, mustn't make a difference
        final double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void BooleanDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final boolean value = random.nextBoolean();
        Feature x = FeatureFactory.newBooleanFeature("x", value);
        Feature y = FeatureFactory.newBooleanFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.BOOLEAN, ox.getType());
        assertEquals(0.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void CategoricalDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final String value = UUID.randomUUID().toString();
        Feature x = FeatureFactory.newCategoricalFeature("x", value);
        Feature y = FeatureFactory.newCategoricalFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.CATEGORICAL, ox.getType());
        assertEquals(0.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void IntegerDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        int value = random.nextInt(1000);
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value + 100);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(Type.NUMBER, oy.getType());
        assertTrue(distance * distance > 0);

        y = FeatureFactory.newNumericalFeature("y", value - 100);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertTrue(distance * distance > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void IntegerDistanceDifferentValueThreshold(int seed) {
        final Random random = new Random(seed);
        int value = random.nextInt(1000);
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value + 100);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.05);

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(Type.NUMBER, oy.getType());
        assertTrue(distance * distance > 0);

        y = FeatureFactory.newNumericalFeature("y", value - 100);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.05);

        assertTrue(distance * distance > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void DoubleDistanceZero(int seed) {
        final Random random = new Random(seed);
        Feature x = FeatureFactory.newNumericalFeature("x", 0.0);
        Feature y = FeatureFactory.newNumericalFeature("y", 1.0);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(Type.NUMBER, oy.getType());
        assertEquals(1, distance);

        y = FeatureFactory.newNumericalFeature("y", -1.0);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        assertEquals(1, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void DoubleDistanceDifferentValueThresholdMet(int seed) {
        final double value = 100.0;
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value - 20.0);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(Type.NUMBER, oy.getType());
        assertTrue(distance * distance > 0);

        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.1);
        assertTrue(distance * distance > 0);

        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.2);
        assertTrue(distance * distance > 0);

        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.3);
        assertFalse(distance * distance > 0);

    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void DoubleDistanceDifferentValueThreshold(int seed) {
        final Random random = new Random(seed);
        double value = random.nextDouble() * 100.0;
        Feature x = FeatureFactory.newNumericalFeature("x", value);
        Feature y = FeatureFactory.newNumericalFeature("y", value + 100.0);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.25);

        assertEquals(Type.NUMBER, ox.getType());
        assertEquals(Type.NUMBER, oy.getType());
        assertTrue(distance * distance > 0);

        y = FeatureFactory.newNumericalFeature("y", value - 100);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.25);

        assertTrue(distance * distance > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void BooleanDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        boolean value = random.nextBoolean();
        Feature x = FeatureFactory.newBooleanFeature("x", value);
        Feature y = FeatureFactory.newBooleanFeature("y", !value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.BOOLEAN, ox.getType());
        assertEquals(Type.BOOLEAN, oy.getType());
        assertEquals(1.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void BooleanDistanceDifferentValueThreshold(int seed) {
        final Random random = new Random(seed);
        boolean value = random.nextBoolean();
        Feature x = FeatureFactory.newBooleanFeature("x", value);
        Feature y = FeatureFactory.newBooleanFeature("y", !value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy, 0.25);

        assertEquals(Type.BOOLEAN, ox.getType());
        assertEquals(Type.BOOLEAN, oy.getType());
        assertEquals(1.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void CategoricalDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        Feature x = FeatureFactory.newCategoricalFeature("x", UUID.randomUUID().toString());
        Feature y = FeatureFactory.newCategoricalFeature("y", UUID.randomUUID().toString());

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.CATEGORICAL, ox.getType());
        assertEquals(Type.CATEGORICAL, oy.getType());
        assertEquals(1.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(1.0, distance);

    }

    @Test
    void differentFeatureTypes() {
        Feature x = FeatureFactory.newCategoricalFeature("x", UUID.randomUUID().toString());
        Feature y = FeatureFactory.newNumericalFeature("y", 0.0);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterFactualScoreCalculator.outputDistance(ox, oy);
        });

        assertEquals("Features must have the same type. Feature 'x', has type 'categorical' and 'number'",
                exception.getMessage());
    }

    @Test
    void unsupportedFeatureType() {
        Feature x = FeatureFactory.newTimeFeature("x", LocalTime.now());
        Feature y = FeatureFactory.newTimeFeature("y", LocalTime.now());

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterFactualScoreCalculator.outputDistance(ox, oy);
        });

        assertEquals("Feature 'x' has unsupported type 'time'", exception.getMessage());
    }

    /**
     * If the goal and the model's output is the same, the distances should all be zero.
     */
    @Test
    void testGoalSizeMatch() throws ExecutionException, InterruptedException {
        final CounterFactualScoreCalculator scoreCalculator = new CounterFactualScoreCalculator();

        PredictionProvider model = TestUtils.getFeatureSkipModel(0);

        List<Feature> features = new ArrayList<>();
        List<FeatureDomain> featureDomains = new ArrayList<>();
        List<Boolean> constraints = new ArrayList<>();

        // f-1
        features.add(FeatureFactory.newNumericalFeature("f-1", 1.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-2
        features.add(FeatureFactory.newNumericalFeature("f-2", 2.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);
        List<CounterfactualEntity> entities = CounterfactualEntityFactory.createEntities(input, domains, constraints, null);

        List<Output> goal = new ArrayList<>();
        goal.add(new Output("f-2", Type.NUMBER, new Value(2.0), 0.0));
        goal.add(new Output("f-3", Type.BOOLEAN, new Value(true), 0.0));

        final CounterfactualSolution solution =
                new CounterfactualSolution(entities, model, goal, UUID.randomUUID(), UUID.randomUUID(), 0.0);

        BendableBigDecimalScore score = scoreCalculator.calculateScore(solution);

        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input)).get();

        assertTrue(score.isFeasible());

        assertEquals(2, goal.size());
        assertEquals(1, predictionOutputs.size()); // A single prediction is expected
        assertEquals(2, predictionOutputs.get(0).getOutputs().size()); // Single prediction with two features
        assertEquals(0, score.getHardScore(0).compareTo(BigDecimal.ZERO));
        assertEquals(0, score.getHardScore(1).compareTo(BigDecimal.ZERO));
        assertEquals(0, score.getHardScore(2).compareTo(BigDecimal.ZERO));
        assertEquals(0, score.getSoftScore(0).compareTo(BigDecimal.ZERO));
        assertEquals(0, score.getSoftScore(1).compareTo(BigDecimal.ZERO));
        assertEquals(3, score.getHardLevelsSize());
        assertEquals(2, score.getSoftLevelsSize());
    }

    /**
     * Using a smaller number of features in the goals (1) than the model's output (2) should
     * throw an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    void testGoalSizeSmaller() throws ExecutionException, InterruptedException {
        final CounterFactualScoreCalculator scoreCalculator = new CounterFactualScoreCalculator();

        PredictionProvider model = TestUtils.getFeatureSkipModel(0);

        List<Feature> features = new ArrayList<>();
        List<FeatureDomain> featureDomains = new ArrayList<>();
        List<Boolean> constraints = new ArrayList<>();

        // f-1
        features.add(FeatureFactory.newNumericalFeature("f-1", 1.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-2
        features.add(FeatureFactory.newNumericalFeature("f-2", 2.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);
        List<CounterfactualEntity> entities = CounterfactualEntityFactory.createEntities(input, domains, constraints, null);

        List<Output> goal = new ArrayList<>();
        goal.add(new Output("f-2", Type.NUMBER, new Value(2.0), 0.0));

        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input)).get();

        assertEquals(1, goal.size());
        assertEquals(1, predictionOutputs.size()); // A single prediction is expected
        assertEquals(2, predictionOutputs.get(0).getOutputs().size()); // Single prediction with two features

        final CounterfactualSolution solution =
                new CounterfactualSolution(entities, model, goal, UUID.randomUUID(), UUID.randomUUID(), 0.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scoreCalculator.calculateScore(solution);
        });

        assertEquals("Prediction size must be equal to goal size", exception.getMessage());

    }

    /**
     * Using a larger number of features in the goals (3) than the model's output (2) should
     * throw an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    void testGoalSizeLarger() throws ExecutionException, InterruptedException {
        final CounterFactualScoreCalculator scoreCalculator = new CounterFactualScoreCalculator();

        PredictionProvider model = TestUtils.getFeatureSkipModel(0);

        List<Feature> features = new ArrayList<>();
        List<FeatureDomain> featureDomains = new ArrayList<>();
        List<Boolean> constraints = new ArrayList<>();

        // f-1
        features.add(FeatureFactory.newNumericalFeature("f-1", 1.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-2
        features.add(FeatureFactory.newNumericalFeature("f-2", 2.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);
        List<CounterfactualEntity> entities = CounterfactualEntityFactory.createEntities(input, domains, constraints, null);

        List<Output> goal = new ArrayList<>();
        goal.add(new Output("f-1", Type.NUMBER, new Value(1.0), 0.0));
        goal.add(new Output("f-2", Type.NUMBER, new Value(2.0), 0.0));
        goal.add(new Output("f-3", Type.BOOLEAN, new Value(true), 0.0));

        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input)).get();

        assertEquals(3, goal.size());
        assertEquals(1, predictionOutputs.size()); // A single prediction is expected
        assertEquals(2, predictionOutputs.get(0).getOutputs().size()); // Single prediction with two features

        final CounterfactualSolution solution =
                new CounterfactualSolution(entities, model, goal, UUID.randomUUID(), UUID.randomUUID(), 0.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scoreCalculator.calculateScore(solution);
        });

        assertEquals("Prediction size must be equal to goal size", exception.getMessage());

    }
}
