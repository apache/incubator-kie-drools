import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.drools.core.config.AbstractRuleConfig;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.services.signal.DefaultSignalManagerHub;

@javax.inject.Singleton
class RuleConfig extends AbstractRuleConfig {

    @javax.inject.Inject
    public RuleConfig(
            Instance<RuleEventListenerConfig> ruleEventListenerConfigs,
            Instance<AgendaEventListener> agendaEventListeners,
            Instance<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        super(ruleEventListenerConfigs, agendaEventListeners, ruleRuntimeEventListeners);
    }
}
