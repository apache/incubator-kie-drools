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
package org.kie.kogito.explainability.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaliencyTest {

    @Test
    void testGetTopFeatures() {
        List<FeatureImportance> fis = new ArrayList<>();
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.19));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), -0.44));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.04));
        Output output = new Output("name", Type.NUMBER);
        Saliency saliency = new Saliency(output, fis);
        List<FeatureImportance> topFeatures = saliency.getTopFeatures(2);
        assertNotNull(topFeatures);
        assertEquals(2, topFeatures.size());
        List<Double> collect = topFeatures.stream().map(FeatureImportance::getScore).collect(Collectors.toList());
        assertTrue(collect.contains(-0.44));
        assertTrue(collect.contains(0.19));
    }

    @Test
    void testGetPositiveFeatures() {
        List<FeatureImportance> fis = new ArrayList<>();
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.19));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), -0.44));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.04));
        Output output = new Output("name", Type.NUMBER);
        Saliency saliency = new Saliency(output, fis);
        List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(2);
        assertNotNull(topFeatures);
        assertEquals(2, topFeatures.size());
        List<Double> collect = topFeatures.stream().map(FeatureImportance::getScore).collect(Collectors.toList());
        assertTrue(collect.contains(0.04));
        assertTrue(collect.contains(0.19));
    }

    @Test
    void testGetNegativeFeatures() {
        List<FeatureImportance> fis = new ArrayList<>();
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.19));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), -0.44));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.04));
        Output output = new Output("name", Type.NUMBER);
        Saliency saliency = new Saliency(output, fis);
        List<FeatureImportance> topFeatures = saliency.getNegativeFeatures(2);
        assertNotNull(topFeatures);
        assertEquals(1, topFeatures.size());
        List<Double> collect = topFeatures.stream().map(FeatureImportance::getScore).collect(Collectors.toList());
        assertTrue(collect.contains(-0.44));
    }

    @Test
    void testSameImportantFeatures() {
        List<FeatureImportance> fis = new ArrayList<>();
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.1));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.1));
        fis.add(new FeatureImportance(TestUtils.getMockedNumericFeature(), 0.1));
        Output output = new Output("name", Type.NUMBER);
        Saliency saliency = new Saliency(output, fis);
        List<FeatureImportance> topFeatures = saliency.getTopFeatures(2);
        assertNotNull(topFeatures);
        assertEquals(2, topFeatures.size());
        List<Double> collect = topFeatures.stream().map(FeatureImportance::getScore).collect(Collectors.toList());
        assertTrue(collect.contains(0.1));
        assertTrue(collect.contains(0.1));
        List<FeatureImportance> negativeFeatures = saliency.getNegativeFeatures(2);
        assertNotNull(negativeFeatures);
        assertTrue(negativeFeatures.isEmpty());
        List<FeatureImportance> positiveFeatures = saliency.getPositiveFeatures(2);
        assertNotNull(positiveFeatures);
        assertEquals(2, positiveFeatures.size());
    }

    @Test
    void testMergeSaliencyMaps() {
        List<FeatureImportance> fis1 = new ArrayList<>();
        fis1.add(new FeatureImportance(FeatureFactory.newTextFeature("f1", "foo"), 0.1));
        fis1.add(new FeatureImportance(FeatureFactory.newTextFeature("f2", "bar"), -0.4));
        fis1.add(new FeatureImportance(FeatureFactory.newNumericalFeature("f3", 10), 0.01));
        Output output1 = new Output("out", Type.NUMBER);
        Saliency saliency1 = new Saliency(output1, fis1);

        List<FeatureImportance> fis2 = new ArrayList<>();
        fis2.add(new FeatureImportance(FeatureFactory.newTextFeature("f1", "foo"), 0.2));
        fis2.add(new FeatureImportance(FeatureFactory.newTextFeature("f2", "bar"), -0.2));
        fis2.add(new FeatureImportance(FeatureFactory.newNumericalFeature("f3", 10), 0.03));
        Output output2 = new Output("out", Type.NUMBER);
        Saliency saliency2 = new Saliency(output2, fis2);
        Map<String, Saliency> map1 = new HashMap<>();
        map1.put("out", saliency1);
        Map<String, Saliency> map2 = new HashMap<>();
        map2.put("out", saliency2);

        Map<String, Saliency> merge = Saliency.merge(List.of(map1, map2));
        assertNotNull(merge);
        assertEquals(1, merge.size());
        Saliency mergedSaliency = merge.get("out");
        List<FeatureImportance> perFeatureImportance = mergedSaliency.getPerFeatureImportance();
        assertNotNull(perFeatureImportance);
        assertEquals(3, perFeatureImportance.size());
        assertEquals(0.15, perFeatureImportance.get(0).getScore(), 1e-3);
        assertEquals(-0.3, perFeatureImportance.get(1).getScore(), 1e-3);
        assertEquals(0.02, perFeatureImportance.get(2).getScore(), 1e-3);
    }
}