import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.decision.DecisionConfig;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.rules.RuleConfig;


public class ApplicationConfig implements org.kie.kogito.Config {

    protected ProcessConfig processConfig;
    protected RuleConfig ruleConfig;
    protected DecisionConfig decisionConfig;

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

    private static <C, L> List<L> merge(Collection<C> configs, Function<C, Collection<L>> configToListeners, Collection<L> listeners) {
        return Stream.concat(
                configs.stream().flatMap(c -> configToListeners.apply(c).stream()),
                listeners.stream()
        ).collect(Collectors.toList());
    }

}
