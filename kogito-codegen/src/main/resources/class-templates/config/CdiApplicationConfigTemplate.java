import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.config.StaticRuleConfig;
import org.kie.kogito.dmn.config.StaticDecisionConfig;
import org.kie.kogito.pmml.config.StaticPredictionConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;

import javax.enterprise.inject.Instance;

@javax.inject.Singleton
public class ApplicationConfig extends org.kie.kogito.StaticConfig {

    @javax.inject.Inject
    public ApplicationConfig(
            Instance<org.kie.kogito.process.ProcessConfig> processConfig,
            Instance<org.kie.kogito.rules.RuleConfig> ruleConfig,
            Instance<org.kie.kogito.decision.DecisionConfig> decisionConfig,
            Instance<org.kie.kogito.prediction.PredictionConfig> predictionConfig) {
        super($Addons$,
              orDefault(processConfig, StaticProcessConfig::new),
              orDefault(ruleConfig, StaticRuleConfig::new),
              orDefault(decisionConfig, StaticDecisionConfig::new),
              orDefault(predictionConfig, StaticPredictionConfig::new));
    }

    private static <T> T orDefault(javax.enterprise.inject.Instance<T> instance, Supplier<T> supplier) {
        if (instance.isUnsatisfied()) {
            return supplier.get();
        } else {
            return instance.get();
        }
    }
}
