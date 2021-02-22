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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.counterfactual.entities.*;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDomain;
import org.kie.kogito.explainability.model.FeatureFactory;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualEntityFactoryTest {

    @Test
    void testIntegerFactory() {
        int value = 5;
        final Feature feature = FeatureFactory.newNumericalFeature("int-feature", value);
        final FeatureDomain domain = FeatureDomain.numerical(0.0, 10.0);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof IntegerEntity);
    }

    @Test
    void testDoubleFactory() {
        double value = 5.0;
        final Feature feature = FeatureFactory.newNumericalFeature("double-feature", value);
        final FeatureDomain domain = FeatureDomain.numerical(0.0, 10.0);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, domain);
        assertTrue(counterfactualEntity instanceof DoubleEntity);
    }

    @Test
    void testBooleanFactory() {
        final Feature feature = FeatureFactory.newBooleanFeature("bool-feature", false);
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, false, FeatureDomain.EMPTY);
        assertTrue(counterfactualEntity instanceof BooleanEntity);
    }

    @Test
    void testCategoricalFactoryObject() {
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", "foo");
        final FeatureDomain domain = FeatureDomain.categorical("foo", "bar");
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
    }

    @Test
    void testCategoricalFactorySet() {
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", "foo");
        final FeatureDomain domain = FeatureDomain.categorical(Set.of("foo", "bar"));
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
    }

    @Test
    void testCategoricalFactoryList() {
        final Feature feature = FeatureFactory.newCategoricalFeature("categorical-feature", "foo");
        final FeatureDomain domain = FeatureDomain.categorical(List.of("foo", "bar"));
        final CounterfactualEntity counterfactualEntity = CounterfactualEntityFactory.from(feature, true, domain);
        assertTrue(counterfactualEntity instanceof CategoricalEntity);
        assertEquals(domain.getCategories(), ((CategoricalEntity) counterfactualEntity).getValueRange());
    }
}