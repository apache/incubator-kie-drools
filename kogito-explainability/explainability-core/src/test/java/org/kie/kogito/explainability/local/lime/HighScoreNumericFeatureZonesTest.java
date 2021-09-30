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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HighScoreNumericFeatureZonesTest {

    @Test
    void testAccept() {
        double tolerance = 0.1d;
        double[] points = new double[] { 1d, 2d };
        HighScoreNumericFeatureZones highScoreNumericFeatureZones = new HighScoreNumericFeatureZones(points, tolerance);

        assertThat(highScoreNumericFeatureZones.test(Double.NaN)).isFalse();

        assertThat(highScoreNumericFeatureZones.test(1.01)).isTrue();
        assertThat(highScoreNumericFeatureZones.test(0.91)).isTrue();
        assertThat(highScoreNumericFeatureZones.test(1.11)).isFalse();
        assertThat(highScoreNumericFeatureZones.test(1.1)).isFalse();
        assertThat(highScoreNumericFeatureZones.test(0.9)).isFalse();

        assertThat(highScoreNumericFeatureZones.test(2.01)).isTrue();
        assertThat(highScoreNumericFeatureZones.test(1.91)).isTrue();
        assertThat(highScoreNumericFeatureZones.test(2.11)).isFalse();
        assertThat(highScoreNumericFeatureZones.test(2.1)).isFalse();
        assertThat(highScoreNumericFeatureZones.test(1.9)).isFalse();
    }
}