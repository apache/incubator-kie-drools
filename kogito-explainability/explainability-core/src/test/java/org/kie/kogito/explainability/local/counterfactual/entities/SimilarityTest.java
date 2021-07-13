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
package org.kie.kogito.explainability.local.counterfactual.entities;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.model.FeatureFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimilarityTest {

    private static double HIGHEST_SIMILARITY = 1.0;
    private static double LOWEST_SIMILARITY = 0.0;

    @Test
    void integerSimpleSimilarity() {
        final int value = 20;
        final IntegerEntity x = IntegerEntity.from(FeatureFactory.newNumericalFeature("x", value), 0, 100);

        x.setProposedValue(value + 9);
        final double similarity1 = x.similarity();

        x.setProposedValue(value - 9);
        final double similarity2 = x.similarity();

        assertEquals(similarity1, similarity2);
        assertTrue(similarity1 > 0.9);
    }

    @Test
    void doubleSimpleSimilarity() {
        final double value = 500.0;
        final DoubleEntity x = DoubleEntity.from(FeatureFactory.newNumericalFeature("x", value), 0.0, 1000.0);

        x.setProposedValue(value + 90.0);
        final double similarity1 = x.similarity();

        x.setProposedValue(value - 90.0);
        final double similarity2 = x.similarity();

        assertEquals(similarity1, similarity2);
        assertTrue(similarity1 > 0.9);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void booleanSimpleSimilarity(int seed) {
        final Random random = new Random(seed);
        final boolean originalValue = random.nextBoolean();
        final BooleanEntity x = BooleanEntity.from(FeatureFactory.newBooleanFeature("x", originalValue));

        x.setProposedValue(!originalValue);
        assertEquals(LOWEST_SIMILARITY, x.similarity());

        x.setProposedValue(originalValue);
        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @Test
    void categoricalSimpleSimilarity() {
        final String value = "foo";
        final Set<String> categories = Set.of("foo", "bar", "baz");
        final CategoricalEntity x = CategoricalEntity.from(FeatureFactory.newCategoricalFeature("x", value), categories);

        x.setProposedValue("bar");
        assertEquals(LOWEST_SIMILARITY, x.similarity());

        x.setProposedValue("baz");
        assertEquals(LOWEST_SIMILARITY, x.similarity());

        x.setProposedValue("foo");
        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void fixedIntegerSimilarity(int seed) {
        final Random random = new Random(seed);
        final int value = random.nextInt();
        final CounterfactualEntity x = FixedIntegerEntity.from(FeatureFactory.newNumericalFeature("x", value));

        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void fixedDoubleSimilarity(int seed) {
        final Random random = new Random(seed);
        final double value = random.nextDouble();
        final CounterfactualEntity x = FixedDoubleEntity.from(FeatureFactory.newNumericalFeature("x", value));

        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void fixedBooleanSimilarity(int seed) {
        final Random random = new Random(seed);
        final boolean value = random.nextBoolean();
        final CounterfactualEntity x = FixedBooleanEntity.from(FeatureFactory.newBooleanFeature("x", value));

        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @RepeatedTest(5)
    void fixedCategoricalSimilarity() {
        final String value = UUID.randomUUID().toString();
        final CounterfactualEntity x = FixedCategoricalEntity.from(FeatureFactory.newCategoricalFeature("x", value));

        assertEquals(HIGHEST_SIMILARITY, x.similarity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void constantRelativeSimilarityIntegerEntity(int seed) {
        final Random random = new Random(seed);
        final int MAX_UPPER_BOUND_RANGE = 10_000;
        // generate 3 random points
        int[] values = new int[3];
        values[0] = random.nextInt(1000);
        values[1] = values[0] + random.nextInt(100);
        values[2] = values[1] + random.nextInt(100);

        // create non-overlapping bounds
        int lowerBound = random.nextInt(values[0] - 1);
        int upperBound = values[2] + random.nextInt(MAX_UPPER_BOUND_RANGE) + 1;
        final IntegerEntity x =
                IntegerEntity.from(FeatureFactory.newNumericalFeature("x", values[0]), lowerBound, upperBound);

        // similarity to point #2
        x.setProposedValue(values[1]);
        final double x_similarity1 = x.similarity();
        // similarity to point #3
        x.setProposedValue(values[2]);
        final double x_similarity2 = x.similarity();

        // change bounds
        lowerBound = random.nextInt(values[0] - 1);
        upperBound = values[2] + random.nextInt(MAX_UPPER_BOUND_RANGE) + 1;

        final IntegerEntity y =
                IntegerEntity.from(FeatureFactory.newNumericalFeature("y", values[0]), lowerBound, upperBound);
        // similarity to point #2 with new range
        y.setProposedValue(values[1]);
        final double y_similarity1 = y.similarity();
        // similarity to point #3 with new range
        y.setProposedValue(values[2]);
        final double y_similarity2 = y.similarity();

        // relative similarities must stay the same
        assertTrue(x_similarity1 > x_similarity2);
        assertTrue(y_similarity1 > y_similarity2);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void constantRelativeSimilarityDoubleEntity(int seed) {
        final Random random = new Random(seed);
        final double MAX_UPPER_BOUND_RANGE = 10_000.0;
        // generate 3 random points
        double[] values = new double[3];
        values[0] = random.nextDouble() * 1000.0;
        values[1] = values[0] + random.nextDouble() * 100.0;
        values[2] = values[1] + random.nextDouble() * 100.0;

        // create non-overlapping bounds
        double lowerBound = random.nextDouble() * values[0];
        double upperBound = values[2] + random.nextDouble() * MAX_UPPER_BOUND_RANGE;
        final DoubleEntity x =
                DoubleEntity.from(FeatureFactory.newNumericalFeature("x", values[0]), lowerBound, upperBound);

        // similarity to point #2
        x.setProposedValue(values[1]);
        final double x_similarity1 = x.similarity();
        // similarity to point #3
        x.setProposedValue(values[2]);
        final double x_similarity2 = x.similarity();

        // change bounds
        lowerBound = random.nextDouble() * values[0];
        upperBound = values[2] + random.nextDouble() * MAX_UPPER_BOUND_RANGE;

        final DoubleEntity y =
                DoubleEntity.from(FeatureFactory.newNumericalFeature("y", values[0]), lowerBound, upperBound);
        // similarity to point #2 with new range
        y.setProposedValue(values[1]);
        final double y_similarity1 = y.similarity();
        // similarity to point #3 with new range
        y.setProposedValue(values[2]);
        final double y_similarity2 = y.similarity();

        // relative similarities must stay the same
        assertTrue(x_similarity1 > x_similarity2);
        assertTrue(y_similarity1 > y_similarity2);
    }
}
