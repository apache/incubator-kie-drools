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
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
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

    @Test
    void BooleanDistanceNull() {
        // Null as a goal
        Feature predictionFeature = FeatureFactory.newBooleanFeature("x", true);
        Feature goalFeature = FeatureFactory.newBooleanFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BOOLEAN, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction

        predictionFeature = FeatureFactory.newBooleanFeature("x", null);
        goalFeature = FeatureFactory.newBooleanFeature("y", false);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BOOLEAN, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal

        predictionFeature = FeatureFactory.newBooleanFeature("x", null);
        goalFeature = FeatureFactory.newBooleanFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BOOLEAN, predictionOutput.getType());
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
    void CategoricalDistanceNull(int seed) {
        final Random random = new Random(seed);
        final String value = UUID.randomUUID().toString();

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newCategoricalFeature("x", value);
        Feature goalFeature = FeatureFactory.newCategoricalFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CATEGORICAL, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction

        predictionFeature = FeatureFactory.newCategoricalFeature("x", null);
        goalFeature = FeatureFactory.newCategoricalFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CATEGORICAL, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal

        predictionFeature = FeatureFactory.newCategoricalFeature("x", null);
        goalFeature = FeatureFactory.newCategoricalFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CATEGORICAL, predictionOutput.getType());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void TextDistanceSameValue(int seed) {
        final String value = UUID.randomUUID().toString();
        Feature x = FeatureFactory.newTextFeature("x", value);
        Feature y = FeatureFactory.newTextFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        final double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.TEXT, ox.getType());
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
    void IntegerDistanceNull(int seed) {
        final Random random = new Random(seed);
        final int value = random.nextInt(1000);

        // Null as a goal
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", value);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", null);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);
            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

        // Null as a prediction
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", null);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", value);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);

            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

        // Null as both prediction and goal
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", null);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", null);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);

            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

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
    void DoubleDistanceNull(int seed) {
        final Random random = new Random(seed);
        final double value = random.nextDouble() * 1000;

        // Null as a goal
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", value);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", null);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);
            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

        // Null as a prediction
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", null);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", value);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);

            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

        // Null as both prediction and goal
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Feature predictionFeature = FeatureFactory.newNumericalFeature("x", null);
            Feature goalFeature = FeatureFactory.newNumericalFeature("x", null);

            Output predictionOutput = outputFromFeature(predictionFeature);
            Output goalOutput = outputFromFeature(goalFeature);

            CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);
        });

        assertEquals("Unsupported NaN or NULL for numeric feature 'x'",
                exception.getMessage());

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

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void currencyDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        Feature x = FeatureFactory.newCurrencyFeature("x", Currency.getInstance("GBP"));
        Feature y = FeatureFactory.newCurrencyFeature("y", Currency.getInstance("EUR"));

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.CURRENCY, ox.getType());
        assertEquals(Type.CURRENCY, oy.getType());
        assertEquals(1.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(1.0, distance);

    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void currencyDistanceNull(int seed) {
        final Random random = new Random(seed);
        final Currency value = Currency.getInstance(Locale.UK);

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newCurrencyFeature("x", value);
        Feature goalFeature = FeatureFactory.newCurrencyFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CURRENCY, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction

        predictionFeature = FeatureFactory.newCurrencyFeature("x", null);
        goalFeature = FeatureFactory.newCurrencyFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CURRENCY, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal

        predictionFeature = FeatureFactory.newCurrencyFeature("x", null);
        goalFeature = FeatureFactory.newCurrencyFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.CURRENCY, predictionOutput.getType());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void currencyDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final Currency value = Currency.getInstance(Locale.US);
        Feature x = FeatureFactory.newCurrencyFeature("x", value);
        Feature y = FeatureFactory.newCurrencyFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.CURRENCY, ox.getType());
        assertEquals(0.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void binaryDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        Feature x = FeatureFactory.newBinaryFeature("x", ByteBuffer.wrap("foo".getBytes()));
        Feature y = FeatureFactory.newBinaryFeature("y", ByteBuffer.wrap("bar".getBytes()));

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.BINARY, ox.getType());
        assertEquals(Type.BINARY, oy.getType());
        assertEquals(1.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(1.0, distance);

    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void binaryDistanceNull(int seed) {
        final Random random = new Random(seed);
        final ByteBuffer value = ByteBuffer.wrap("foo".getBytes());

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newBinaryFeature("x", value);
        Feature goalFeature = FeatureFactory.newBinaryFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BINARY, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction

        predictionFeature = FeatureFactory.newBinaryFeature("x", null);
        goalFeature = FeatureFactory.newBinaryFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BINARY, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal

        predictionFeature = FeatureFactory.newBinaryFeature("x", null);
        goalFeature = FeatureFactory.newBinaryFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.BINARY, predictionOutput.getType());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void objectDistanceDifferentValue(int seed) {
        Random random = new Random(seed);
        Feature x = FeatureFactory.newObjectFeature("x", "test");
        Feature y = FeatureFactory.newObjectFeature("y", 20);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.UNDEFINED, ox.getType());
        assertEquals(Type.UNDEFINED, oy.getType());
        assertEquals(1.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(1.0, distance);

    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void objectDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final ByteBuffer value = ByteBuffer.wrap("foo".getBytes());
        Feature x = FeatureFactory.newObjectFeature("x", value);
        Feature y = FeatureFactory.newObjectFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.UNDEFINED, ox.getType());
        assertEquals(0.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void objectDistanceNull(int seed) {
        final Random random = new Random(seed);
        final ByteBuffer value = ByteBuffer.wrap("foo".getBytes());

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newObjectFeature("x", value);
        Feature goalFeature = FeatureFactory.newObjectFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.UNDEFINED, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction

        predictionFeature = FeatureFactory.newObjectFeature("x", null);
        goalFeature = FeatureFactory.newObjectFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.UNDEFINED, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal

        predictionFeature = FeatureFactory.newObjectFeature("x", null);
        goalFeature = FeatureFactory.newObjectFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.UNDEFINED, predictionOutput.getType());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void binaryDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final ByteBuffer value = ByteBuffer.wrap("foo".getBytes());
        Feature x = FeatureFactory.newBinaryFeature("x", value);
        Feature y = FeatureFactory.newBinaryFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.BINARY, ox.getType());
        assertEquals(0.0, distance);

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, distance);
    }

    @Test
    void durationDistanceDifferentValue() {
        final double SECONDS = 120L;
        Feature x = FeatureFactory.newDurationFeature("x", Duration.ZERO);
        Feature y = FeatureFactory.newDurationFeature("y", Duration.ofSeconds((long) SECONDS));

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.DURATION, ox.getType());
        assertEquals(Type.DURATION, oy.getType());

        assertEquals(SECONDS, distance);

        x = FeatureFactory.newDurationFeature("x", Duration.ofSeconds((long) SECONDS));
        y = FeatureFactory.newDurationFeature("y", Duration.ofDays(1L));
        ox = outputFromFeature(x);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        assertEquals(0.9986, distance, 0.01);

        x = FeatureFactory.newDurationFeature("x", Duration.ofDays(2L));
        y = FeatureFactory.newDurationFeature("y", Duration.ofDays(1L));
        ox = outputFromFeature(x);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        System.out.println(distance);
        assertEquals(0.5, distance, 1e-4);
    }

    @Test
    void durationDistanceNull() {
        final Duration value = Duration.ofHours(72L);

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newDurationFeature("x", value);
        Feature goalFeature = FeatureFactory.newDurationFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.DURATION, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction
        predictionFeature = FeatureFactory.newDurationFeature("x", null);
        goalFeature = FeatureFactory.newDurationFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.DURATION, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal
        predictionFeature = FeatureFactory.newDurationFeature("x", null);
        goalFeature = FeatureFactory.newDurationFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.DURATION, predictionOutput.getType());
        System.out.println(distance);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void durationDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final Duration value = Duration.ofSeconds(random.nextLong());
        Feature x = FeatureFactory.newDurationFeature("x", value);
        Feature y = FeatureFactory.newDurationFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.DURATION, ox.getType());
        assertEquals(0.0, Math.abs(distance));

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, Math.abs(distance));
    }

    @Test
    void timeDistanceDifferentValue() {
        final LocalTime value = LocalTime.now();
        Feature x = FeatureFactory.newTimeFeature("x", LocalTime.of(15, 59));
        Feature y = FeatureFactory.newTimeFeature("y", LocalTime.of(10, 1));

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.TIME, ox.getType());
        assertEquals(Type.TIME, oy.getType());

        assertEquals(0.248, distance, 0.01);

        x = FeatureFactory.newTimeFeature("x", LocalTime.of(12, 0));
        y = FeatureFactory.newTimeFeature("y", LocalTime.of(12, 57));
        ox = outputFromFeature(x);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        assertEquals(0.039, distance, 0.01);

        x = FeatureFactory.newTimeFeature("x", LocalTime.of(0, 0));
        y = FeatureFactory.newTimeFeature("y", LocalTime.of(15, 17));
        ox = outputFromFeature(x);
        oy = outputFromFeature(y);
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy);
        assertEquals(0.636, distance, 0.01);
    }

    @Test
    void timeDistanceNull() {
        final LocalTime value = LocalTime.of(17, 17);

        // Null as a goal
        Feature predictionFeature = FeatureFactory.newTimeFeature("x", value);
        Feature goalFeature = FeatureFactory.newTimeFeature("y", null);

        Output predictionOutput = outputFromFeature(predictionFeature);
        Output goalOutput = outputFromFeature(goalFeature);

        double distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.TIME, goalOutput.getType());
        assertEquals(1.0, distance);

        // Null as a prediction
        predictionFeature = FeatureFactory.newTimeFeature("x", null);
        goalFeature = FeatureFactory.newTimeFeature("y", value);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.TIME, predictionOutput.getType());
        assertEquals(1.0, distance);

        // Null as both prediction and goal
        predictionFeature = FeatureFactory.newTimeFeature("x", null);
        goalFeature = FeatureFactory.newTimeFeature("y", null);

        predictionOutput = outputFromFeature(predictionFeature);
        goalOutput = outputFromFeature(goalFeature);

        distance = CounterFactualScoreCalculator.outputDistance(predictionOutput, goalOutput);

        assertEquals(Type.TIME, predictionOutput.getType());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void timeDistanceSameValue(int seed) {
        final Random random = new Random(seed);
        final LocalTime value = LocalTime.of(random.nextInt(24), random.nextInt(60));
        Feature x = FeatureFactory.newTimeFeature("x", value);
        Feature y = FeatureFactory.newTimeFeature("y", value);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.TIME, ox.getType());
        assertEquals(0.0, Math.abs(distance));

        // Use a random threshold, mustn't make a difference
        distance = CounterFactualScoreCalculator.outputDistance(ox, oy, random.nextDouble());

        assertEquals(0.0, Math.abs(distance));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void TextDistanceDifferentValue(int seed) {
        final Random random = new Random(seed);
        Feature x = FeatureFactory.newTextFeature("x", UUID.randomUUID().toString());
        Feature y = FeatureFactory.newTextFeature("y", UUID.randomUUID().toString());

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        double distance = CounterFactualScoreCalculator.outputDistance(ox, oy);

        assertEquals(Type.TEXT, ox.getType());
        assertEquals(Type.TEXT, oy.getType());
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

        Feature x = FeatureFactory.newVectorFeature("x", 1, 2, 3, 4);
        Feature y = FeatureFactory.newVectorFeature("y", 5, 6, 7, 8);

        Output ox = outputFromFeature(x);
        Output oy = outputFromFeature(y);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterFactualScoreCalculator.outputDistance(ox, oy);
        });

        assertEquals("Feature 'x' has unsupported type 'vector'", exception.getMessage());
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

    /**
     * Null values for input Boolean features should be accepted as valid
     */
    @Test
    void testNullBooleanInput() throws ExecutionException, InterruptedException {
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
        features.add(FeatureFactory.newBooleanFeature("f-2", null));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);
        List<CounterfactualEntity> entities = CounterfactualEntityFactory.createEntities(input, domains, constraints, null);

        List<Output> goal = new ArrayList<>();
        goal.add(new Output("f-2", Type.BOOLEAN, new Value(null), 0.0));
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
     * Null values for input Integer features should not be accepted as valid
     */
    @Test
    void testNullIntegerInput() throws ExecutionException, InterruptedException {
        List<Feature> features = new ArrayList<>();
        List<FeatureDomain> featureDomains = new ArrayList<>();
        List<Boolean> constraints = new ArrayList<>();

        // f-1
        features.add(FeatureFactory.newNumericalFeature("f-1", 1.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-2
        features.add(FeatureFactory.newNumericalFeature("f-2", null));
        featureDomains.add(NumericalFeatureDomain.create(0, 10));
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.createEntities(input, domains, constraints, null);
        });

        assertEquals("Null numeric features are not supported in counterfactuals", exception.getMessage());
    }

    /**
     * Null values for input Double features should not be accepted as valid
     */
    @Test
    void testNullDoubleInput() {
        List<Feature> features = new ArrayList<>();
        List<FeatureDomain> featureDomains = new ArrayList<>();
        List<Boolean> constraints = new ArrayList<>();

        // f-1
        features.add(FeatureFactory.newNumericalFeature("f-1", 1.0));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-2
        features.add(FeatureFactory.newNumericalFeature("f-2", null));
        featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
        constraints.add(false);

        // f-3
        features.add(FeatureFactory.newBooleanFeature("f-3", true));
        featureDomains.add(EmptyFeatureDomain.create());
        constraints.add(false);

        PredictionInput input = new PredictionInput(features);
        PredictionFeatureDomain domains = new PredictionFeatureDomain(featureDomains);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.createEntities(input, domains, constraints, null);
        });

        assertEquals("Null numeric features are not supported in counterfactuals", exception.getMessage());
    }

    /**
     * Test precision errors for primary soft score.
     * When the primary soft score is calculated between features with the same numerical
     * value a similarity of 1 is expected. For a large number of features, due to floating point errors this distance may be
     * in some cases slightly larger than 1, which will cause the distance (Math.sqrt(1.0-similarity)) to cause an exception.
     * The score calculation method should not let this should not occur.
     */
    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testPrimarySoftScore(int seed) {
        final Random random = new Random(seed);
        final List<Feature> features = new ArrayList<>();
        final List<FeatureDomain> featureDomains = new ArrayList<>();
        final List<Boolean> constraints = new ArrayList<>();

        final int nFeatures = 1000;
        // Create a large number of identical features
        for (int n = 0; n < nFeatures; n++) {
            features.add(FeatureFactory.newNumericalFeature("f-" + n, random.nextDouble() * 1e-100));
            featureDomains.add(NumericalFeatureDomain.create(0.0, 10.0));
            constraints.add(false);
        }

        final PredictionInput input = new PredictionInput(features);
        final PredictionFeatureDomain domain = new PredictionFeatureDomain(featureDomains);
        final List<CounterfactualEntity> entities =
                CounterfactualEntityFactory.createEntities(input, domain, constraints, null);

        // Create score calculator and model
        final CounterFactualScoreCalculator scoreCalculator = new CounterFactualScoreCalculator();
        PredictionProvider model = TestUtils.getFeatureSkipModel(0);

        // Create goal
        final List<Output> goal = new ArrayList<>();
        for (int n = 1; n < nFeatures; n++) {
            goal.add(new Output("f-" + n, Type.NUMBER, features.get(n).getValue(), 1.0));
        }

        final CounterfactualSolution solution =
                new CounterfactualSolution(entities, model, goal, UUID.randomUUID(), UUID.randomUUID(), 0.0);

        final BendableBigDecimalScore score = scoreCalculator.calculateScore(solution);

        assertEquals(0.0, score.getSoftScore(0).doubleValue(), 1e-5);
    }
}
