import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.pmml.config.AbstractPredictionConfig;
import org.kie.kogito.prediction.PredictionEventListenerConfig;

@org.springframework.stereotype.Component
class PredictionConfig extends AbstractPredictionConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public PredictionConfig(
            java.util.List<org.kie.kogito.prediction.PredictionEventListenerConfig> predictionEventListenerConfigs) {
        super(predictionEventListenerConfigs);
    }

}
