import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.config.StaticRuleConfig;
import org.kie.kogito.decision.DecisionConfig;
import org.kie.kogito.pmml.config.StaticPredictionConfig;
import org.kie.kogito.prediction.PredictionConfig;
import org.kie.kogito.dmn.config.StaticDecisionConfig;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.rules.RuleConfig;

public class ApplicationConfig implements org.kie.kogito.Config {

    private ProcessConfig processConfig = new StaticProcessConfig();
    private RuleConfig ruleConfig = new StaticRuleConfig();
    private DecisionConfig decisionConfig = new StaticDecisionConfig();
    private PredictionConfig predictionConfig = new StaticPredictionConfig();

    @Override
    public ProcessConfig process() {
        return processConfig;
    }

    @Override
    public RuleConfig rule() {
        return ruleConfig;
    }

    @Override
    public DecisionConfig decision() {
        return decisionConfig;
    }

    @Override
    public PredictionConfig prediction() {
        return predictionConfig;
    }

}
