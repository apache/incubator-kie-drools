package $Package$;

import javax.enterprise.inject.Instance;

import org.kie.kogito.drools.core.config.AbstractRuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.rules.RuleEventListenerConfig;

@javax.inject.Singleton
class RuleConfig extends AbstractRuleConfig implements org.kie.kogito.rules.RuleConfig {

    @javax.inject.Inject
    public RuleConfig(
            Instance<RuleEventListenerConfig> ruleEventListenerConfigs,
            Instance<AgendaEventListener> agendaEventListeners,
            Instance<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        super(ruleEventListenerConfigs, agendaEventListeners, ruleRuntimeEventListeners);
    }
}
