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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

import static org.assertj.core.api.Assertions.assertThat;

class LimeConfigEntityFactoryTest {

    @Test
    void testEmptySolutionConversion() {
        LimeConfigSolution solution = new LimeConfigSolution();
        LimeConfig limeConfig = LimeConfigEntityFactory.toLimeConfig(solution);
        assertThat(limeConfig).isNotNull();
    }

    @Test
    void testConversion() {
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        LimeConfig config = new LimeConfig();
        List<Prediction> predictions = Collections.emptyList();
        List<LimeConfigEntity> entities = Collections.emptyList();
        LimeConfigSolution solution = new LimeConfigSolution(config, predictions, entities, model);
        LimeConfig limeConfig = LimeConfigEntityFactory.toLimeConfig(solution);
        assertThat(limeConfig).isNotNull();
    }

    @Test
    void testWeightingEntities() {
        List<? extends LimeConfigEntity> entities = LimeConfigEntityFactory.createWeightingEntities(new LimeConfig());
        assertThat(entities).isNotNull().hasSize(1);
    }

    @Test
    void testSamplingEntities() {
        List<? extends LimeConfigEntity> entities = LimeConfigEntityFactory.createSamplingEntities(new LimeConfig());
        assertThat(entities).isNotNull().hasSize(4);
    }

    @Test
    void testEncodingEntities() {
        List<? extends LimeConfigEntity> entities = LimeConfigEntityFactory.createEncodingEntities(new LimeConfig());
        assertThat(entities).isNotNull().hasSize(2);
    }

    @Test
    void testProximityEntities() {
        List<? extends LimeConfigEntity> entities = LimeConfigEntityFactory.createProximityEntities(new LimeConfig());
        assertThat(entities).isNotNull().hasSize(4);
    }
}