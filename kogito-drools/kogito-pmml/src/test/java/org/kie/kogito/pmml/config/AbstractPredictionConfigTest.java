package org.kie.kogito.pmml.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.prediction.PredictionEventListenerConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AbstractPredictionConfigTest {

    @Test
    void predictionEventListenersWithoutPredictionEventListenerConfigs() {
        AbstractPredictionConfig abstractPredictionConfig = getAbstractPredictionConfig(null);
        assertNull(abstractPredictionConfig.predictionEventListeners());
    }

    @Test
    void predictionEventListenersWithPredictionEventListenerConfigs() {
        final List<PredictionEventListenerConfig> predictionEventListenerConfigs = IntStream
                .range(0,3)
                .mapToObj(i -> getPredictionEventListenerConfig())
                .collect(Collectors.toList());
        AbstractPredictionConfig abstractPredictionConfig = getAbstractPredictionConfig(predictionEventListenerConfigs);
        assertEquals(predictionEventListenerConfigs.get(0), abstractPredictionConfig.predictionEventListeners());
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