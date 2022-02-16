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
package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class CompositeFeatureUtilsTest {

    @Test
    void testAlreadyLinear() {
        final List<Feature> features = new ArrayList<>();

        features.add(FeatureFactory.newTextFeature("f-1", "foo"));
        features.add(FeatureFactory.newNumericalFeature("f-2", 100.0));
        features.add(FeatureFactory.newBooleanFeature("f-3", true));

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        // Test flattening
        assertEquals(features, flattened);
        assertNotSame(features, flattened);
        assertEquals(features.size(), flattened.size());

        // Test unflattening
        final List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(features, unflattened);
        assertNotSame(features, unflattened);
        assertEquals(features.size(), unflattened.size());
    }

    @Test
    void testSingleCompositeList() {

        final Feature composite = FeatureFactory.newCompositeFeature("composite",
                List.of(
                        FeatureFactory.newTextFeature("f-1", "foo"),
                        FeatureFactory.newNumericalFeature("f-2", 100.0),
                        FeatureFactory.newBooleanFeature("f-3", true)));

        final List<Feature> features = List.of(composite);

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        // Test flattening
        assertEquals(1, features.size());
        assertEquals(3, flattened.size());
        assertEquals("f-1", flattened.get(0).getName());
        assertEquals("f-2", flattened.get(1).getName());
        assertEquals("f-3", flattened.get(2).getName());

        // Test unflattening
        final List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(1, unflattened.size());
        assertEquals(features, unflattened);
    }

    @Test
    void testMixedWithCompositeList() {
        final List<Feature> features = new ArrayList<>();

        final Feature composite = FeatureFactory.newCompositeFeature("composite",
                List.of(
                        FeatureFactory.newTextFeature("f-1", "foo"),
                        FeatureFactory.newNumericalFeature("f-2", 100.0),
                        FeatureFactory.newBooleanFeature("f-3", true)));

        features.add(FeatureFactory.newNumericalFeature("g-1", 200.0));
        features.add(composite);

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        // Test flattening
        assertEquals(4, flattened.size());
        assertEquals("g-1", flattened.get(0).getName());
        assertEquals("f-1", flattened.get(1).getName());

        // Test unflattening
        final List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(2, unflattened.size());
        assertEquals(features, unflattened);
        assertNotSame(features, unflattened);
        assertEquals("g-1", unflattened.get(0).getName());
        assertEquals("composite", unflattened.get(1).getName());
        assertEquals(3, ((List<Feature>) unflattened.get(1).getValue().getUnderlyingObject()).size());
        assertEquals("f-1", ((List<Feature>) unflattened.get(1).getValue().getUnderlyingObject()).get(0).getName());
        assertEquals("f-2", ((List<Feature>) unflattened.get(1).getValue().getUnderlyingObject()).get(1).getName());
        assertEquals("f-3", ((List<Feature>) unflattened.get(1).getValue().getUnderlyingObject()).get(2).getName());
    }

    @Test
    void testFixedCompositeMap() {
        List<Map<String, Object>> transactions = new LinkedList<>();
        Map<String, Object> t1 = new HashMap<>();
        t1.put("Card Type", "Prepaid");
        t1.put("Location", "Global");
        t1.put("Amount", 146.0);
        t1.put("Auth Code", "Approved");
        transactions.add(t1);
        Map<String, Object> t2 = new HashMap<>();
        t2.put("Card Type", "Debit");
        t2.put("Location", "Local");
        t2.put("Amount", 512.0);
        t2.put("Auth Code", "Denied");
        transactions.add(t2);
        Map<String, Object> map = new HashMap<>();
        map.put("Transactions", transactions);

        final List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("composite", map));

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        // Test flattening
        assertEquals(8, flattened.size());

        // Test unflattening
        List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(1, unflattened.size());
        assertEquals(features, unflattened);
    }

    @Test
    void testVariableCompositeMap() {
        List<Map<String, Object>> transactions = new LinkedList<>();
        Map<String, Object> t1 = new HashMap<>();
        t1.put("Card Type", "Prepaid");
        t1.put("Location", "Global");
        t1.put("Amount",
                FeatureFactory.newNumericalFeature("Amount", 146.0,
                        NumericalFeatureDomain.create(0, 1000)));
        t1.put("Auth Code", FeatureFactory.newBooleanFeature("Auth Code", true, EmptyFeatureDomain.create()));
        transactions.add(t1);
        Map<String, Object> t2 = new HashMap<>();
        t2.put("Card Type", "Debit");
        t2.put("Location", "Local");
        t2.put("Amount", FeatureFactory.newNumericalFeature("Amount", 512.0,
                NumericalFeatureDomain.create(0, 1000)));
        t2.put("Auth Code", FeatureFactory.newBooleanFeature("Auth Code", false, EmptyFeatureDomain.create()));
        transactions.add(t2);
        Map<String, Object> map = new HashMap<>();
        map.put("Transactions", transactions);

        final List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("composite", map));

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        // Test flattening
        assertEquals(8, flattened.size());

        // Test unflattening
        List<Feature> unflattened = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(1, unflattened.size());
        assertEquals(features, unflattened);
    }
}