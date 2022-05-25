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
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.EncodingParams;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class LimeConfigTest {

    @Test
    void testNumericEncodingParams() {
        LimeConfig config = new LimeConfig()
                .withEncodingParams(new EncodingParams(0.01, 2));
        assertThat(config.getEncodingParams().getNumericTypeClusterGaussianFilterWidth()).isEqualTo(0.01);
        assertThat(config.getEncodingParams().getNumericTypeClusterThreshold()).isEqualTo(2);
    }

    @Test
    void testNormalizeWeights() {
        LimeConfig config = new LimeConfig()
                .withNormalizeWeights(false);
        assertThat(config.isNormalizeWeights()).isFalse();

        config = new LimeConfig()
                .withNormalizeWeights(true);
        assertThat(config.isNormalizeWeights()).isTrue();

        config = new LimeConfig();
        assertThat(config.isNormalizeWeights()).isFalse();
    }

    @Test
    void testBoostrapInputs() {
        LimeConfig config = new LimeConfig().witBootstrapInputs(10);
        assertThat(config.getBoostrapInputs()).isEqualTo(10);
    }

    @Test
    void testAdaptiveVariance() {
        LimeConfig config = new LimeConfig().withAdaptiveVariance(false);
        assertThat(config.isAdaptDatasetVariance()).isFalse();

        config = new LimeConfig().withAdaptiveVariance(true);
        assertThat(config.isAdaptDatasetVariance()).isTrue();
    }

    @Test
    void testDataDistribution() {
        DataDistribution dd = mock(DataDistribution.class);
        LimeConfig config = new LimeConfig().withDataDistribution(dd);
        assertThat(config.getDataDistribution()).isEqualTo(dd);
    }

    @Test
    void testHighScoreFeatureZones() {
        LimeConfig config = new LimeConfig().withHighScoreFeatureZones(false);
        assertThat(config.isHighScoreFeatureZones()).isFalse();

        config = new LimeConfig().withHighScoreFeatureZones(true);
        assertThat(config.isHighScoreFeatureZones()).isTrue();
    }

    @Test
    void testPenalizeBalanceSparse() {
        LimeConfig config = new LimeConfig().withPenalizeBalanceSparse(false);
        assertThat(config.isPenalizeBalanceSparse()).isFalse();

        config = new LimeConfig().withPenalizeBalanceSparse(true);
        assertThat(config.isPenalizeBalanceSparse()).isTrue();
    }

    @Test
    void testRetries() {
        LimeConfig config = new LimeConfig().withRetries(5);
        assertThat(config.getNoOfRetries()).isEqualTo(5);
    }

    @Test
    void testProximityFilter() {
        LimeConfig config = new LimeConfig().withProximityFilter(false);
        assertThat(config.isProximityFilter()).isFalse();

        config = new LimeConfig().withProximityFilter(true);
        assertThat(config.isProximityFilter()).isTrue();
    }

    @Test
    void testFeatureSelection() {
        LimeConfig config = new LimeConfig().withFeatureSelection(false);
        assertThat(config.isFeatureSelection()).isFalse();

        config = new LimeConfig().withFeatureSelection(true);
        assertThat(config.isFeatureSelection()).isTrue();
        assertThat(new LimeConfig().withFeatureSelection(false)).isNotEqualTo(new LimeConfig().withFeatureSelection(true));
    }

    @Test
    void testFeatures() {
        LimeConfig config = new LimeConfig().withNoOfFeatures(5);
        assertThat(config.getNoOfFeatures()).isEqualTo(5);
        assertThat(new LimeConfig().withNoOfFeatures(5)).isNotEqualTo(new LimeConfig().withNoOfFeatures(4));
    }

    @Test
    void testEquals() {
        LimeConfig c1 = new LimeConfig();
        LimeConfig c1Copy = c1.copy();
        LimeConfig c2 = new LimeConfig();
        LimeConfig c3 = new LimeConfig().withHighScoreFeatureZones(false);
        assertThat(c1).isEqualTo(c1Copy);
        assertThat(c1).isNotEqualTo(c2);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo(c3);
    }
}
