package org.kie.kogito.pmml.config;

import org.kie.kogito.prediction.PredictionEventListenerConfig;

public abstract class AbstractPredictionConfig implements org.kie.kogito.prediction.PredictionConfig {

    private final PredictionEventListenerConfig predictionEventListener;

    protected AbstractPredictionConfig(
            Iterable<PredictionEventListenerConfig> predictionEventListenerConfigs) {
        if (predictionEventListenerConfigs != null && predictionEventListenerConfigs.iterator().hasNext()) {
            this.predictionEventListener = predictionEventListenerConfigs.iterator().next();
        } else {
            this.predictionEventListener = null;
        }
    }

    public PredictionEventListenerConfig predictionEventListeners() {
        return predictionEventListener;
    }
}
