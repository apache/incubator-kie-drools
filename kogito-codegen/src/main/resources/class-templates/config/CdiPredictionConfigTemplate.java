import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.kie.kogito.pmml.config.AbstractPredictionConfig;
import org.kie.kogito.prediction.PredictionEventListenerConfig;

@javax.inject.Singleton
class PredictionConfig extends AbstractPredictionConfig {

    @javax.inject.Inject
    public PredictionConfig(
            Instance<PredictionEventListenerConfig> predictionEventListenerConfigs) {
        super(predictionEventListenerConfigs);
    }

}
