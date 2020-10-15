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

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataUtilsTest {

    private final static Random random = new Random();

    @BeforeAll
    static void setupBefore() {
        random.setSeed(4);
    }

    @Test
    void testDataGeneration() {
        double mean = 0.5;
        double stdDeviation = 0.1;
        int size = 100;
        double[] data = DataUtils.generateData(mean, stdDeviation, size, random);

        assertEquals(mean, DataUtils.getMean(data), 1e-2);
        assertEquals(stdDeviation, DataUtils.getStdDev(data, mean), 1e-2);

        // check the sum of deviations from mean is zero
        double sum = 0;
        for (double d : data) {
            sum += d - mean;
        }
        assertEquals(0, sum, 1e-4);
    }

    @Test
    void testGetMean() {
        double[] data = new double[5];
        data[0] = 2;
        data[1] = 4;
        data[2] = 3;
        data[3] = 5;
        data[4] = 1;
        assertEquals(3, DataUtils.getMean(data), 1e-6);
    }

    @Test
    void testGetStdDev() {
        double[] data = new double[5];
        data[0] = 2;
        data[1] = 4;
        data[2] = 3;
        data[3] = 5;
        data[4] = 1;
        assertEquals(1.41, DataUtils.getStdDev(data, 3), 1e-2);
    }

    @Test
    void testGaussianKernel() {
        double x = 0.218;
        double k = DataUtils.gaussianKernel(x, 0, 1);
        assertEquals(0.389, k, 1e-3);
    }

    @Test
    void testEuclideanDistance() {
        double[] x = new double[]{1, 1};
        double[] y = new double[]{2, 3};
        double distance = DataUtils.euclideanDistance(x, y);
        assertEquals(2.236, distance, 1e-3);

        assertTrue(Double.isNaN(DataUtils.euclideanDistance(x, new double[0])));
    }

    @Test
    void testHammingDistanceDouble() {
        double[] x = new double[]{2, 1};
        double[] y = new double[]{2, 3};
        double distance = DataUtils.hammingDistance(x, y);
        assertEquals(1, distance, 1e-1);

        assertTrue(Double.isNaN(DataUtils.hammingDistance(x, new double[0])));
    }

    @Test
    void testHammingDistanceString() {
        String x = "test1";
        String y = "test2";
        double distance = DataUtils.hammingDistance(x, y);
        assertEquals(1, distance, 1e-1);

        assertTrue(Double.isNaN(DataUtils.hammingDistance(x, "testTooLong")));
    }

    @Test
    void testExponentialSmoothingKernel() {
        double x = 0.218;
        double k = DataUtils.exponentialSmoothingKernel(x, 2);
        assertEquals(0.994, k, 1e-3);
    }

    @Test
    void testPerturbFeaturesEmpty() {
        List<Feature> features = new LinkedList<>();
        PerturbationContext perturbationContext = new PerturbationContext(random, 0);
        List<Feature> newFeatures = DataUtils.perturbFeatures(features, perturbationContext);
        assertNotNull(newFeatures);
        assertEquals(features.size(), newFeatures.size());
    }

    @Test
    void testPerturbDropNumericZero() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f0", 1));
        features.add(FeatureFactory.newNumericalFeature("f1", 3.14));
        features.add(FeatureFactory.newNumericalFeature("f2", 5));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropNumeric(input, 0);
    }

    @Test
    void testPerturbDropNumericOne() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f0", 1));
        features.add(FeatureFactory.newNumericalFeature("f1", 3.14));
        features.add(FeatureFactory.newNumericalFeature("f2", 0.55));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropNumeric(input, 1);
    }

    @Test
    void testPerturbDropNumericTwo() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f0", 1));
        features.add(FeatureFactory.newNumericalFeature("f1", 3.14));
        features.add(FeatureFactory.newNumericalFeature("f2", 0.55));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropNumeric(input, 2);
    }

    @Test
    void testPerturbDropNumericThree() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f0", 1));
        features.add(FeatureFactory.newNumericalFeature("f1", 3.14));
        features.add(FeatureFactory.newNumericalFeature("f2", 0.55));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropNumeric(input, 3);
    }

    @Test
    void testPerturbDropStringZero() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropString(input, 0);
    }

    @Test
    void testPerturbDropStringOne() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropString(input, 1);
    }

    @Test
    void testPerturbDropStringTwo() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropString(input, 2);
    }

    @Test
    void testPerturbDropStringThree() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(features);
        assertPerturbDropString(input, 3);
    }

    @Test
    void testPerturbDropCompositeStringZero() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(List.of(FeatureFactory.newCompositeFeature("composite", features)));
        assertPerturbDropString(input, 0);
    }

    @Test
    void testPerturbDropCompositeStringOne() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(List.of(FeatureFactory.newCompositeFeature("composite", features)));
        assertPerturbDropString(input, 1);
    }

    @Test
    void testPerturbDropCompositeStringTwo() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(List.of(FeatureFactory.newCompositeFeature("composite", features)));
        assertPerturbDropString(input, 2);
    }

    @Test
    void testPerturbDropCompositeStringThree() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f0", "foo"));
        features.add(FeatureFactory.newTextFeature("f1", "foo bar"));
        features.add(FeatureFactory.newTextFeature("f2", " "));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar "));
        PredictionInput input = new PredictionInput(List.of(FeatureFactory.newCompositeFeature("composite", features)));
        assertPerturbDropString(input, 3);
    }

    private void assertPerturbDropNumeric(PredictionInput input, int noOfPerturbations) {
        List<Feature> newFeatures = DataUtils.perturbFeatures(input.getFeatures(), new PerturbationContext(random, noOfPerturbations));
        int changedFeatures = 0;
        for (int i = 0; i < input.getFeatures().size(); i++) {
            double v = input.getFeatures().get(i).getValue().asNumber();
            double pv = newFeatures.get(i).getValue().asNumber();
            if (v != pv) {
                changedFeatures++;
            }
        }
        assertThat(changedFeatures).isBetween((int) Math.min(noOfPerturbations, input.getFeatures().size() * 0.5),
                                              (int) Math.max(noOfPerturbations, input.getFeatures().size() * 0.5));
    }

    private void assertPerturbDropString(PredictionInput input, int noOfPerturbations) {
        List<Feature> newFeatures = DataUtils.perturbFeatures(input.getFeatures(), new PerturbationContext(random, noOfPerturbations));
        int changedFeatures = 0;
        for (int i = 0; i < input.getFeatures().size(); i++) {
            String v = input.getFeatures().get(i).getValue().asString();
            String pv = newFeatures.get(i).getValue().asString();
            if (!v.equals(pv)) {
                changedFeatures++;
            }
        }
        assertThat(changedFeatures).isBetween((int) Math.min(noOfPerturbations, input.getFeatures().size() * 0.5),
                                              (int) Math.max(noOfPerturbations, input.getFeatures().size() * 0.5));
    }

    @Test
    void testDoublesToFeatures() {
        double[] inputs = new double[10];
        for (int i = 0; i < 10; i++) {
            inputs[i] = i % 2 == 0 ? 1 : 0;
        }
        List<Feature> features = DataUtils.doublesToFeatures(inputs);
        assertNotNull(features);
        assertEquals(10, features.size());
        for (Feature f : features) {
            assertNotNull(f);
            assertNotNull(f.getName());
            assertEquals(Type.NUMBER, f.getType());
            assertNotNull(f.getValue());
        }
    }

    @Test
    void testDoubleToFeature() {
        double d = 0.5;
        Feature f = DataUtils.doubleToFeature(d);
        assertNotNull(f);
        assertNotNull(f.getName());
        assertEquals(Type.NUMBER, f.getType());
        assertNotNull(f.getValue());
    }

    @Test
    void testRandomDistributionGeneration() {
        DataDistribution dataDistribution = DataUtils.generateRandomDataDistribution(10, 10, random);
        assertNotNull(dataDistribution);
        assertNotNull(dataDistribution.getFeatureDistributions());
        for (FeatureDistribution featureDistribution : dataDistribution.getFeatureDistributions()) {
            assertNotNull(featureDistribution);
        }
    }

    @Test
    void testGetFeatureDistribution() {
        double[] doubles = new double[10];
        Arrays.fill(doubles, 1);
        FeatureDistribution featureDistribution = DataUtils.getFeatureDistribution(doubles);
        assertNotNull(featureDistribution);
    }

    @Test
    void testLinearizedNumericFeatures() {
        List<Feature> features = new LinkedList<>();
        Feature f = TestUtils.getMockedNumericFeature();
        features.add(f);
        List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(features);
        assertEquals(features.size(), linearizedFeatures.size());
    }

    @Test
    void testLinearizedTextFeatures() {
        List<Feature> features = new LinkedList<>();
        Feature f = TestUtils.getMockedTextFeature("foo bar ");
        features.add(f);
        List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(features);
        assertEquals(1, linearizedFeatures.size());
    }

    @Test
    void testCompositeLinearizedFeatures() {
        List<Feature> features = new LinkedList<>();
        List<Feature> list = new LinkedList<>();
        list.add(FeatureFactory.newTextFeature("f0", "foo bar"));
        list.add(FeatureFactory.newFulltextFeature("f0", "foo bar", s -> Arrays.asList(s.split(" "))));
        list.add(FeatureFactory.newCategoricalFeature("f0", "1"));
        list.add(FeatureFactory.newBooleanFeature("f1", true));
        list.add(FeatureFactory.newNumericalFeature("f2", 13));
        list.add(FeatureFactory.newDurationFeature("f3", Duration.ofDays(13)));
        list.add(FeatureFactory.newTimeFeature("f4", LocalTime.now()));
        list.add(FeatureFactory.newObjectFeature("f5", new float[]{0.4f, 0.4f}));
        list.add(FeatureFactory.newObjectFeature("f6", FeatureFactory.newObjectFeature("nf-0", new Object())));
        Feature f = FeatureFactory.newCompositeFeature("name", list);
        features.add(f);
        List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(features);
        assertEquals(10, linearizedFeatures.size());
    }

    @Test
    void testDropFeature() {
        for (Type t : Type.values()) {
            Feature target = TestUtils.getMockedFeature(t, new Value<>(1d));
            List<Feature> features = new LinkedList<>();
            features.add(TestUtils.getMockedNumericFeature());
            features.add(target);
            features.add(TestUtils.getMockedTextFeature("foo bar"));
            features.add(TestUtils.getMockedNumericFeature());
            List<Feature> newFeatures = DataUtils.dropFeature(features, target);
            assertNotEquals(features, newFeatures);
        }
    }

    @Test
    void testDropLinearizedFeature() {
        for (Type t : Type.values()) {
            Feature target = TestUtils.getMockedFeature(t, new Value<>(1d));
            List<Feature> features = new LinkedList<>();
            features.add(TestUtils.getMockedNumericFeature());
            features.add(target);
            features.add(TestUtils.getMockedTextFeature("foo bar"));
            features.add(TestUtils.getMockedNumericFeature());
            Feature source = FeatureFactory.newCompositeFeature("composite", features);
            Feature newFeature = DataUtils.dropOnLinearizedFeatures(target, source);
            assertNotEquals(source, newFeature);
        }
    }
}