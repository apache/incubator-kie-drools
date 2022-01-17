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

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.counterfactual.entities.BooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.IntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBinaryEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCompositeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCurrencyEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDurationEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTextEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTimeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedURIEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedVectorEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.CategoricalFeatureDomain;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualEntityFactoryTest {

    @Test
    void testIntegerFactory() {
        final int value = 5;
        final Feature feature = FeatureFactory.newNumericalFeature("int-feature", value);
        final FeatureDomain domain = NumericalFeatureDomain.create(0.0, 10.0);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof IntegerEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testFixedIntegerFactory() {
        final int value = 5;
        final Feature feature = FeatureFactory.newNumericalFeature("int-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedIntegerEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testDoubleFactory() {
        final double value = 5.5;
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", value);
        final FeatureDomain domain = NumericalFeatureDomain.create(0.0, 10.0);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof DoubleEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testFixedDoubleFactory() {
        final double value = 5.5;
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedDoubleEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testBooleanFactory() {
        final boolean value = false;
        final Feature feature = FeatureFactory.newBooleanFeature("bool-feature", value);
        final CounterfactualEntity counterfactualEntity =
                CounterfactualEntityFactory.from(feature, false, EmptyFeatureDomain.create());
        assertTrue(counterfactualEntity instanceof BooleanEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testFixedBooleanFactory() {
        final boolean value = false;
        final Feature feature = FeatureFactory.newBooleanFeature("bool-feature", value);
        final CounterfactualEntity counterfactualEntity =
                CounterfactualEntityFactory.from(feature, true, EmptyFeatureDomain.create());
        assertTrue(counterfactualEntity instanceof FixedBooleanEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testCategoricalFactoryObject() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value);
        final FeatureDomain domain = CategoricalFeatureDomain.create("foo", "bar");
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testFixedCategoricalEntity() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedCategoricalEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testCategoricalFactorySet() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value);
        final FeatureDomain domain = CategoricalFeatureDomain.create(Set.of("foo", "bar"));
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testCategoricalFactoryList() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value);
        final FeatureDomain domain = CategoricalFeatureDomain.create(List.of("foo", "bar"));
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testBinaryFactory() {
        final ByteBuffer value = ByteBuffer.allocate(256);
        final Feature feature = FeatureFactory.newBinaryFeature("binary-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedBinaryEntity);
        assertEquals(Type.BINARY, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: binary",
                exception.getMessage());
    }

    @Test
    void testCompositeFactory() {
        Map<String, Object> map = new HashMap<>();
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newObjectFeature("f1", new Object()));
        features.add(FeatureFactory.newTextFeature("f2", "hola"));
        features.add(FeatureFactory.newFulltextFeature("f3", "foo bar"));
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
        final Feature feature = FeatureFactory.newCompositeFeature("composite-feature", map);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedCompositeEntity);
        assertEquals(Type.COMPOSITE, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: composite",
                exception.getMessage());
    }

    @Test
    void testCurrencyFactory() {
        final Currency value = Currency.getInstance(Locale.ITALY);
        final Feature feature = FeatureFactory.newCurrencyFeature("currrency-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedCurrencyEntity);
        assertEquals(Type.CURRENCY, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: currency",
                exception.getMessage());
    }

    @Test
    void testDurationFactory() {
        final Duration value = Duration.ofDays(1);
        final Feature feature = FeatureFactory.newDurationFeature("duration-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedDurationEntity);
        assertEquals(Type.DURATION, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: duration",
                exception.getMessage());
    }

    @Test
    void testTextFactory() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newTextFeature("text-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedTextEntity);
        assertEquals(Type.TEXT, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: text",
                exception.getMessage());
    }

    @Test
    void testTimeFactory() {
        final LocalTime value = LocalTime.now();
        final Feature feature = FeatureFactory.newTimeFeature("time-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedTimeEntity);
        assertEquals(Type.TIME, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: time",
                exception.getMessage());
    }

    @Test
    void testURIFactory() {
        final URI value = URI.create("./");
        final Feature feature = FeatureFactory.newURIFeature("uri-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedURIEntity);
        assertEquals(Type.URI, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: uri",
                exception.getMessage());
    }

    @Test
    void testVectorFactory() {
        final double[] value = new double[10];
        Arrays.fill(value, 1d);
        final Feature feature = FeatureFactory.newVectorFeature("uri-feature", value);
        final FeatureDomain domain = EmptyFeatureDomain.create();
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof FixedVectorEntity);
        assertEquals(Type.VECTOR, counterfactualEntity.asFeature().getType());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, false, domain);
        });

        assertEquals("Unsupported feature type: vector",
                exception.getMessage());
    }

    @Test
    void testCreateFixedEntities() {
        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureDomains = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.1));
        constraints.add(true);
        featureDomains.add(EmptyFeatureDomain.create());

        features.add(FeatureFactory.newNumericalFeature("f-num2", 100.2));
        constraints.add(false);
        featureDomains.add(NumericalFeatureDomain.create(0.0, 1000.0));

        features.add(FeatureFactory.newNumericalFeature("f-num3", 100.3));
        constraints.add(true);
        featureDomains.add(EmptyFeatureDomain.create());

        features.add(FeatureFactory.newNumericalFeature("f-num4", 100.4));
        constraints.add(false);
        featureDomains.add(NumericalFeatureDomain.create(0.0, 1000.0));

        PredictionFeatureDomain featureDomain = new PredictionFeatureDomain(featureDomains);

        PredictionInput input = new PredictionInput(features);

        List<CounterfactualEntity> entities =
                CounterfactualEntityFactory.createEntities(input, featureDomain, constraints, null);

        // Check types
        assertTrue(entities.get(0) instanceof FixedDoubleEntity);
        assertTrue(entities.get(1) instanceof DoubleEntity);
        assertTrue(entities.get(2) instanceof FixedDoubleEntity);
        assertTrue(entities.get(3) instanceof DoubleEntity);

        // Check values
        assertEquals(100.1, entities.get(0).asFeature().getValue().asNumber());
        assertEquals(100.2, entities.get(1).asFeature().getValue().asNumber());
        assertEquals(100.3, entities.get(2).asFeature().getValue().asNumber());
        assertEquals(100.4, entities.get(3).asFeature().getValue().asNumber());

        // Check constraints
        assertTrue(entities.get(0).isConstrained());
        assertFalse(entities.get(1).isConstrained());
        assertTrue(entities.get(2).isConstrained());
        assertFalse(entities.get(3).isConstrained());
    }

    @Test
    void testValidateNullNumericalFeature() {
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", null);
        final FeatureDomain domain = EmptyFeatureDomain.create();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature, true, domain);
        });

        assertEquals("Null numeric features are not supported in counterfactuals", exception.getMessage());
    }
}
