/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.local.shap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionInput;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapSyntheticDataSampleTest {

    // generate a specific shap sample from scratch
    private ShapSyntheticDataSample generateShapSample() {
        List<Feature> fs = new ArrayList<>();
        fs.add(FeatureFactory.newNumericalFeature("f1", -1));
        fs.add(FeatureFactory.newNumericalFeature("f2", -1));
        fs.add(FeatureFactory.newNumericalFeature("f3", -1));
        fs.add(FeatureFactory.newNumericalFeature("f4", -1));
        fs.add(FeatureFactory.newNumericalFeature("f5", -1));
        PredictionInput pi = new PredictionInput(fs);

        boolean[] mask = { true, true, false, false, true };
        RealMatrix background = MatrixUtils.createRealMatrix(new double[][] {
                { 0., 1., 2., 3., 4. },
                { 5., 6., 7., 8., 9. }
        });
        double weight = .5;
        boolean fixed = true;

        return new ShapSyntheticDataSample(pi, mask, background, weight, fixed);
    }

    // generate some sample expected synthetic data
    private List<PredictionInput> generateExpectedSynthData() {
        List<Feature> synthFeatures1 = new ArrayList<>();
        List<Feature> synthFeatures2 = new ArrayList<>();
        List<PredictionInput> synthData = new ArrayList<>();

        synthFeatures1.add(FeatureFactory.newNumericalFeature("f1", -1));
        synthFeatures1.add(FeatureFactory.newNumericalFeature("f2", -1));
        synthFeatures1.add(FeatureFactory.newNumericalFeature("f3", 2.));
        synthFeatures1.add(FeatureFactory.newNumericalFeature("f4", 3.));
        synthFeatures1.add(FeatureFactory.newNumericalFeature("f5", -1));
        synthData.add(new PredictionInput(synthFeatures1));

        synthFeatures2.add(FeatureFactory.newNumericalFeature("f1", -1));
        synthFeatures2.add(FeatureFactory.newNumericalFeature("f2", -1));
        synthFeatures2.add(FeatureFactory.newNumericalFeature("f3", 7.));
        synthFeatures2.add(FeatureFactory.newNumericalFeature("f4", 8.));
        synthFeatures2.add(FeatureFactory.newNumericalFeature("f5", -1));
        synthData.add(new PredictionInput(synthFeatures2));

        return synthData;
    }

    // Test that everything recovers as expected
    @Test
    void testSyntheticCreation() {
        ShapSyntheticDataSample shapSamp = generateShapSample();
        List<PredictionInput> expectedSynth = generateExpectedSynthData();
        List<PredictionInput> generatedSynth = shapSamp.getSyntheticData();
        for (int i = 0; i < generatedSynth.size(); i++) {
            List<Feature> expectedFeatures = expectedSynth.get(i).getFeatures();
            List<Feature> generatedFeatures = generatedSynth.get(i).getFeatures();
            for (int j = 0; j < generatedFeatures.size(); j++) {
                assertEquals(generatedFeatures.get(j), expectedFeatures.get(j));
            }
        }
    }

    @Test
    void testIsFixed() {
        ShapSyntheticDataSample shapSamp = generateShapSample();
        assertTrue(shapSamp.isFixed());
    }

    @Test
    void testGetMask() {
        ShapSyntheticDataSample shapSamp = generateShapSample();
        boolean[] mask = { true, true, false, false, true };
        assertArrayEquals(mask, shapSamp.getMask());
    }

    @Test
    void testWeight() {
        ShapSyntheticDataSample shapSamp = generateShapSample();
        assertEquals(.5, shapSamp.getWeight());
        shapSamp.incrementWeight();
        assertEquals(1.5, shapSamp.getWeight());
        shapSamp.setWeight(2.5);
        assertEquals(2.5, shapSamp.getWeight());
    }
}
