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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.counterfactual.entities.BooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.IntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
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
