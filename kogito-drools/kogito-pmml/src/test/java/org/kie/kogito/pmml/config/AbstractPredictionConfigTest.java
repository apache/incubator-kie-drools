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
package org.kie.kogito.pmml.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.prediction.PredictionEventListenerConfig;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractPredictionConfigTest {

    @Test
    void predictionEventListenersWithoutPredictionEventListenerConfigs() {
        AbstractPredictionConfig abstractPredictionConfig = getAbstractPredictionConfig(null);
        assertThat(abstractPredictionConfig.predictionEventListeners()).isNull();
    }

    @Test
    void predictionEventListenersWithPredictionEventListenerConfigs() {
        final List<PredictionEventListenerConfig> predictionEventListenerConfigs = IntStream
                .range(0, 3)
                .mapToObj(i -> getPredictionEventListenerConfig())
                .collect(Collectors.toList());
        AbstractPredictionConfig abstractPredictionConfig = getAbstractPredictionConfig(predictionEventListenerConfigs);
        assertThat(abstractPredictionConfig.predictionEventListeners()).isEqualTo(predictionEventListenerConfigs.get(0));
    }

    private AbstractPredictionConfig getAbstractPredictionConfig(Iterable<PredictionEventListenerConfig> predictionEventListenerConfigs) {
        return new AbstractPredictionConfig(predictionEventListenerConfigs) {
            @Override
            public PredictionEventListenerConfig predictionEventListeners() {
                return super.predictionEventListeners();
            }
        };
    }

    private PredictionEventListenerConfig getPredictionEventListenerConfig() {
        return new PredictionEventListenerConfig() {

        };
    }
}