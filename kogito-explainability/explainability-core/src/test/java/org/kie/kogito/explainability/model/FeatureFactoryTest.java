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

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeatureFactoryTest {

    private void assertFeature(Type type, Object object, Feature feature) {
        assertNotNull(feature);
        assertNotNull(feature.getName());
        assertNotNull(feature.getType());
        assertEquals(type, feature.getType());
        assertNotNull(feature.getValue());
        assertNotNull(feature.getValue().getUnderlyingObject());
        if (Type.COMPOSITE.equals(type)) {
            Collection<Feature> objectCollection = (Collection<Feature>) object;
            Collection<Feature> featureCollection = (Collection<Feature>) feature.getValue().getUnderlyingObject();
            for (Feature f : objectCollection) {
                assertThat(f).isIn(featureCollection);
            }
        } else {
            assertEquals(object, feature.getValue().getUnderlyingObject());
        }
    }

    @Test
    void testTimeFeature() {
        LocalTime time = LocalTime.now();
        String name = "some-name";
        Feature feature = FeatureFactory.newTimeFeature(name, time);
        assertFeature(Type.TIME, time, feature);
    }

    @Test
    void testCategoricalFeature() {
        String name = "some-name";
        String category = "FIXED-CAT";
        Feature feature = FeatureFactory.newCategoricalFeature(name, category);
        assertFeature(Type.CATEGORICAL, category, feature);
    }

    @Test
    void testNumberFeature() {
        String name = "some-name";
        Number number = 0.1d;
        Feature feature = FeatureFactory.newNumericalFeature(name, number);
        assertFeature(Type.NUMBER, number, feature);
    }

    @Test
    void testBooleanFeature() {
        String name = "some-name";
        Feature feature = FeatureFactory.newBooleanFeature(name, false);
        assertFeature(Type.BOOLEAN, false, feature);
    }

    @Test
    void testCurrencyFeature() {
        String name = "some-name";
        Currency currency = Currency.getInstance(Locale.getDefault());
        Feature feature = FeatureFactory.newCurrencyFeature(name, currency);
        assertFeature(Type.CURRENCY, currency, feature);
    }

    @Test
    void testBinaryFeature() {
        String name = "some-name";
        ByteBuffer binary = ByteBuffer.allocate(256);
        Feature feature = FeatureFactory.newBinaryFeature(name, binary);
        assertFeature(Type.BINARY, binary, feature);
    }

    @Test
    void testURIFeature() {
        String name = "some-name";
        URI uri = URI.create("./");
        Feature feature = FeatureFactory.newURIFeature(name, uri);
        assertFeature(Type.URI, uri, feature);
    }

    @Test
    void testDurationFeature() {
        String name = "some-name";
        Duration duration = Duration.ofDays(1);
        Feature feature = FeatureFactory.newDurationFeature(name, duration);
        assertFeature(Type.DURATION, duration, feature);
    }

    @Test
    void testTextFeature() {
        String name = "some-name";
        String text = "some text value";
        Feature feature = FeatureFactory.newTextFeature(name, text);
        assertFeature(Type.TEXT, text, feature);
    }

    @Test
    void testVectorFeature() {
        String name = "some-name";
        double[] vector = new double[10];
        Arrays.fill(vector, 1d);
        Feature feature = FeatureFactory.newVectorFeature(name, vector);
        assertFeature(Type.VECTOR, vector, feature);
    }

    @Test
    void testObjectFeature() {
        String name = "some-name";
        Object object = new Object();
        Feature feature = FeatureFactory.newObjectFeature(name, object);
        assertFeature(Type.UNDEFINED, object, feature);
    }

    @Test
    void testNestedCompositeFeature() {
        Map<String, Object> map = new HashMap<>();
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newObjectFeature("f1", new Object()));
        features.add(FeatureFactory.newTextFeature("f2", "hola"));
        features.add(FeatureFactory.newTextFeature("f3", "foo bar"));
        features.add(FeatureFactory.newNumericalFeature("f4", 131));
        features.add(FeatureFactory.newBooleanFeature("f5", false));
        features.add(FeatureFactory.newDurationFeature("f6", Duration.ofDays(2)));
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nf-1", "nested text");
        nestedMap.put("nf-2", ByteBuffer.allocate(1024));
        features.add(FeatureFactory.newCompositeFeature("f7", nestedMap));
        for (Feature f : features) {
            map.put(f.getName(), f.getValue().getUnderlyingObject());
        }
        String name = "some-name";
        Feature feature = FeatureFactory.newCompositeFeature(name, map);
        assertFeature(Type.COMPOSITE, features, feature);
    }
}