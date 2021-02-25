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
package org.kie.kogito.explainability.local.lime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class ProximityFilterTest {

    @Test
    void testFilter() {
        ProximityFilter filter = new ProximityFilter(0.5, 0.1);
        int size = 10;
        List<Pair<double[], Double>> trainingSet = new ArrayList<>();
        double[] weights = new double[size];
        TestUtils.fillBalancedDataForFiltering(size, trainingSet, weights);
        filter.apply(trainingSet, weights);
        assertThat(trainingSet.size()).isEqualTo(5);
    }

    @Test
    void testNoFilterWithNonMatchingSizes() {
        ProximityFilter filter = new ProximityFilter(0.5, 0.1);
        int size = 10;
        List<Pair<double[], Double>> trainingSet = new ArrayList<>();
        double[] weights = new double[size];
        TestUtils.fillBalancedDataForFiltering(size, trainingSet, weights);
        trainingSet.remove(0);
        filter.apply(trainingSet, weights);
        assertThat(trainingSet.size()).isEqualTo(9); // filtering doesn't happen because of non matching sizes
    }

    @Test
    void testFilterNull() {
        ProximityFilter filter = new ProximityFilter(0.5, 0.1);
        assertThatCode(() -> filter.apply(null, null)).doesNotThrowAnyException();
    }

}
