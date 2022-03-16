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
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.counterfactual.entities.BinaryEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.BooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.local.counterfactual.entities.CurrencyEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.DurationEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.IntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.LongEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.ObjectEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.TimeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.URIEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBinaryEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCompositeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCurrencyEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDurationEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedLongEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedObjectEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTextEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTimeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedURIEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedVectorEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.NumericFeatureDistribution;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.BinaryFeatureDomain;
import org.kie.kogito.explainability.model.domain.CategoricalFeatureDomain;
import org.kie.kogito.explainability.model.domain.CurrencyFeatureDomain;
import org.kie.kogito.explainability.model.domain.DurationFeatureDomain;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.explainability.model.domain.ObjectFeatureDomain;
import org.kie.kogito.explainability.model.domain.TimeFeatureDomain;
import org.kie.kogito.explainability.model.domain.URIFeatureDomain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualEntityFactoryTest {

    @Test
    void testIntegerFactory() {
        final int value = 5;
        final FeatureDomain domain = NumericalFeatureDomain.create(0.0, 10.0);
        final Feature feature = FeatureFactory.newNumericalFeature("int-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof IntegerEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testFixedIntegerFactory() {
        final int value = 5;
        final Feature feature = FeatureFactory.newNumericalFeature("int-feature", value);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedIntegerEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testLongFactory() {
        final long value = 5;
        final FeatureDomain domain = NumericalFeatureDomain.create(0.0, 10.0);
        final Feature feature = FeatureFactory.newNumericalFeature("long-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertNotNull(counterfactualEntity);
        assertTrue(counterfactualEntity instanceof LongEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testFixedLongFactory() {
        final long value = 5;
        final Feature feature = FeatureFactory.newNumericalFeature("long-feature", value);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertNotNull(counterfactualEntity);
        assertTrue(counterfactualEntity instanceof FixedLongEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testDoubleFactory() {
        final double value = 5.5;
        final FeatureDomain domain = NumericalFeatureDomain.create(0.0, 10.0);
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof DoubleEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testFixedDoubleFactory() {
        final double value = 5.5;
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", value);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedDoubleEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().asNumber());
    }

    @Test
    void testBooleanFactory() {
        final boolean value = false;
        final Feature feature = FeatureFactory.newBooleanFeature("bool-feature", value, EmptyFeatureDomain.create());
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof BooleanEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testFixedBooleanFactory() {
        final boolean value = false;
        final Feature feature = FeatureFactory.newBooleanFeature("bool-feature", value);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedBooleanEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testCategoricalFactoryObject() {
        final String value = "foo";
        final FeatureDomain domain = CategoricalFeatureDomain.create("foo", "bar");
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testFixedCategoricalEntity() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedCategoricalEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testCategoricalFactorySet() {
        final String value = "foo";
        final FeatureDomain domain = CategoricalFeatureDomain.create(Set.of("foo", "bar"));
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testCategoricalFactoryList() {
        final String value = "foo";
        final FeatureDomain domain = CategoricalFeatureDomain.create(List.of("foo", "bar"));
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", value, domain);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().toString());
    }

    @Test
    void testBinaryFactory() {
        final ByteBuffer value = ByteBuffer.allocate(256);
        Feature feature = FeatureFactory.newBinaryFeature("binary-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedBinaryEntity);
        assertEquals(Type.BINARY, counterfactualEntity.asFeature().getType());

        final List<ByteBuffer> categories = Stream.of(
                "bar".getBytes(), "baz".getBytes(), "fun".getBytes())
                .map(ByteBuffer::wrap).collect(Collectors.toList());

        FeatureDomain domain = BinaryFeatureDomain.create(categories);
        feature = FeatureFactory.newBinaryFeature("binary-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof BinaryEntity);
        assertEquals(domain.getCategories(), ((BinaryEntity) counterfactualEntity).getValueRange());

        domain = BinaryFeatureDomain.create(new HashSet<>(categories));
        feature = FeatureFactory.newBinaryFeature("binary-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertEquals(domain.getCategories(), ((BinaryEntity) counterfactualEntity).getValueRange());

        domain = BinaryFeatureDomain.create(ByteBuffer.wrap("bar".getBytes()),
                ByteBuffer.wrap("baz".getBytes()), ByteBuffer.wrap("fun".getBytes()));
        feature = FeatureFactory.newBinaryFeature("binary-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertEquals(domain.getCategories(), ((BinaryEntity) counterfactualEntity).getValueRange());

        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
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
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedCompositeEntity);
        assertEquals(Type.COMPOSITE, counterfactualEntity.asFeature().getType());
    }

    @Test
    void testCurrencyFactory() {
        final Currency value = Currency.getInstance(Locale.ITALY);
        Feature feature = FeatureFactory.newCurrencyFeature("currrency-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedCurrencyEntity);
        assertEquals(Type.CURRENCY, counterfactualEntity.asFeature().getType());

        final Feature fixedFeature = FeatureFactory.newCurrencyFeature("currrency-feature", value);
        FeatureDomain domain = CurrencyFeatureDomain.create(Currency.getAvailableCurrencies());
        feature = FeatureFactory.newCurrencyFeature("currrency-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CurrencyEntity);
        assertEquals(domain.getCategories(), ((CurrencyEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        domain = CurrencyFeatureDomain.create(new ArrayList<>(Currency.getAvailableCurrencies()));
        feature = FeatureFactory.newCurrencyFeature("currrency-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CurrencyEntity);
        assertEquals(domain.getCategories(), ((CurrencyEntity) counterfactualEntity).getValueRange());
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        Currency[] currencies = List.of(Locale.ITALY, Locale.UK, Locale.US).stream().map(Currency::getInstance).collect(
                Collectors.toList()).toArray(new Currency[0]);
        domain = CurrencyFeatureDomain.create(currencies);
        feature = FeatureFactory.newCurrencyFeature("currrency-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof CurrencyEntity);
        assertEquals(currencies.length, ((CurrencyEntity) counterfactualEntity).getValueRange().size());
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

    }

    @Test
    void testDurationFactory() {
        final Duration value = Duration.ofDays(1);
        Feature feature = FeatureFactory.newDurationFeature("duration-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedDurationEntity);
        assertEquals(Type.DURATION, counterfactualEntity.asFeature().getType());

        FeatureDomain domain = DurationFeatureDomain.create(0, 60, ChronoUnit.SECONDS);
        feature = FeatureFactory.newDurationFeature("duration-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof DurationEntity);
        assertEquals(Type.DURATION, counterfactualEntity.asFeature().getType());
        assertFalse(counterfactualEntity.isConstrained());

        CounterfactualEntity entity = DurationEntity.from(feature, Duration.ZERO, Duration.ofDays(2));
        assertEquals(0, entity.distance());
        assertTrue(((DurationEntity) entity).getValueRange().contains(1e5));
        assertFalse(((DurationEntity) entity).getValueRange().contains(2e5));
        assertFalse(entity.isConstrained());

        entity = DurationEntity.from(feature, Duration.ZERO, Duration.ofDays(2), false);
        assertEquals(0, entity.distance());
        assertFalse(entity.isConstrained());

        FeatureDistribution distribution = new NumericFeatureDistribution(feature, new Random().doubles(10).toArray());
        entity = DurationEntity.from(feature, Duration.ZERO, Duration.ofDays(2), distribution);
        assertEquals(0, entity.distance());
        assertFalse(entity.isConstrained());

    }

    @Test
    void testTextFactory() {
        final String value = "foo";
        final Feature feature = FeatureFactory.newTextFeature("text-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedTextEntity);
        assertEquals(Type.TEXT, counterfactualEntity.asFeature().getType());

        final Feature varyingFeature = FeatureFactory.newTextFeature("text-feature", value, EmptyFeatureDomain.create());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(varyingFeature);
        });

        assertEquals("Unsupported feature type: text",
                exception.getMessage());
    }

    @Test
    void testTimeFactory() {
        final LocalTime value = LocalTime.now();
        Feature feature = FeatureFactory.newTimeFeature("time-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedTimeEntity);
        assertEquals(Type.TIME, counterfactualEntity.asFeature().getType());

        FeatureDomain domain = TimeFeatureDomain.create(value.minusHours(10), value.plusHours(10));
        feature = FeatureFactory.newTimeFeature("time-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof TimeEntity);
        assertEquals(Type.TIME, counterfactualEntity.asFeature().getType());
        assertEquals(value, ((TimeEntity) counterfactualEntity).getProposedValue());
    }

    @Test
    void testURIFactory() throws URISyntaxException {
        final URI value = URI.create("./");
        Feature feature = FeatureFactory.newURIFeature("uri-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedURIEntity);
        assertEquals(Type.URI, counterfactualEntity.asFeature().getType());

        FeatureDomain domain = URIFeatureDomain.create(new URI("./"), new URI("../"), new URI("https://example.com"));
        feature = FeatureFactory.newURIFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof URIEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        domain = URIFeatureDomain.create(List.of(new URI("./"), new URI("../"), new URI("https://example.com")));
        feature = FeatureFactory.newURIFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof URIEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        domain = URIFeatureDomain.create(Set.of(new URI("./"), new URI("../"), new URI("https://example.com")));
        feature = FeatureFactory.newURIFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof URIEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testObjectFactory() {
        final URI value = URI.create("./");
        Feature feature = FeatureFactory.newObjectFeature("f", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedObjectEntity);
        assertEquals(Type.UNDEFINED, counterfactualEntity.asFeature().getType());

        FeatureDomain domain = ObjectFeatureDomain.create("test", 45L);
        feature = FeatureFactory.newObjectFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof ObjectEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        domain = ObjectFeatureDomain.create(List.of("test", 45L));
        feature = FeatureFactory.newObjectFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof ObjectEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());

        domain = ObjectFeatureDomain.create(Set.of("test", 45L));
        feature = FeatureFactory.newObjectFeature("uri-feature", value, domain);
        counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof ObjectEntity);
        assertEquals(value, counterfactualEntity.asFeature().getValue().getUnderlyingObject());
    }

    @Test
    void testVectorFactory() {
        final double[] value = new double[10];
        Arrays.fill(value, 1d);
        final Feature feature = FeatureFactory.newVectorFeature("uri-feature", value);
        CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature);
        assertTrue(counterfactualEntity instanceof FixedVectorEntity);
        assertEquals(Type.VECTOR, counterfactualEntity.asFeature().getType());
    }

    @Test
    void testCreateFixedEntities() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f-num1", 100.1));
        features.add(FeatureFactory.newNumericalFeature("f-num2", 100.2, NumericalFeatureDomain.create(0.0, 1000.0)));
        features.add(FeatureFactory.newNumericalFeature("f-num3", 100.3));
        features.add(FeatureFactory.newNumericalFeature("f-num4", 100.4, NumericalFeatureDomain.create(0.0, 1000.0)));

        PredictionInput input = new PredictionInput(features);

        List<CounterfactualEntity> entities =
                CounterfactualEntityFactory.createEntities(input);

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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CounterfactualEntityFactory.from(feature);
        });

        assertEquals("Null numeric features are not supported in counterfactuals", exception.getMessage());
    }
}
