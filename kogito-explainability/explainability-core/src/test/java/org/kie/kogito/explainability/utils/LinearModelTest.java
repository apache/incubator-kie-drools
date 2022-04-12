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
package org.kie.kogito.explainability.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class LinearModelTest {

    private static final Random random = new Random();

    @Test
    void testEmptyFitClassificationDoesNothing() {
        int size = 10;
        LinearModel linearModel = new LinearModel(size, true, random);
        Collection<Pair<double[], Double>> trainingSet = new LinkedList<>();
        linearModel.fit(trainingSet);
        assertArrayEquals(new double[size], linearModel.getWeights());
    }

    @Test
    void testEmptyFitRegressionDoesNothing() {
        int size = 10;
        LinearModel linearModel = new LinearModel(size, false, random);
        Collection<Pair<double[], Double>> trainingSet = new LinkedList<>();
        linearModel.fit(trainingSet);
        assertArrayEquals(new double[size], linearModel.getWeights());
    }

    @Test
    void testRegressionFit() {
        int size = 10;
        LinearModel linearModel = new LinearModel(size, false, random);
        Collection<Pair<double[], Double>> trainingSet = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            double[] x = new double[size];
            for (int j = 0; j < size; j++) {
                x[j] = (double) i / (1d * j + i);
            }
            Double y = DoubleStream.of(x).sum();
            trainingSet.add(new ImmutablePair<>(x, y));
        }
        assertThat(linearModel.fit(trainingSet)).isLessThan(1d);
    }

    @Test
    void testClassificationFit() {
        int size = 10;
        LinearModel linearModel = new LinearModel(size, true, random);
        Collection<Pair<double[], Double>> trainingSet = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            double[] x = new double[size];
            for (int j = 0; j < size; j++) {
                x[j] = (double) i / (1d * j + i);
            }
            Double y = i % 2 == 0 ? 1d : 0d;
            trainingSet.add(new ImmutablePair<>(x, y));
        }
        assertThat(linearModel.fit(trainingSet)).isLessThan(1d);
    }
}