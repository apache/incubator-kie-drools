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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericFeatureDistributionTest {

    @Test
    void testNumericSamples() {
        Feature feature = TestUtils.getMockedNumericFeature();
        double[] doubles = DataUtils.generateSamples(0, 10, 10);
        List<Value<?>> values = Arrays.stream(doubles).mapToObj(Value::new).collect(Collectors.toList());
        GenericFeatureDistribution numericFeatureDistribution = new GenericFeatureDistribution(feature, values);
        assertEquals(10, numericFeatureDistribution.getAllSamples().size());
        assertEquals(3, numericFeatureDistribution.sample(3).size());
        assertThat(numericFeatureDistribution.sample().asNumber()).isBetween(0d, 10d);
        assertEquals(19, numericFeatureDistribution.sample(19).size());
    }

    @Test
    void testStringSample() {
        Feature feature = TestUtils.getMockedNumericFeature();
        String[] words = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
        List<Value<?>> values = Arrays.stream(words).map(Value::new).collect(Collectors.toList());
        GenericFeatureDistribution stringFeatureDistribution = new GenericFeatureDistribution(feature, values);
        assertEquals(26, stringFeatureDistribution.getAllSamples().size());
        assertEquals(3, stringFeatureDistribution.sample(3).size());
        assertThat(stringFeatureDistribution.sample().asString()).isBetween("a", "z");
        assertEquals(55, stringFeatureDistribution.sample(55).size());
    }
}