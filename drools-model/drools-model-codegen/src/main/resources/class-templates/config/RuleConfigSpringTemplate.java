package $Package$;

import java.util.List;

import org.kie.kogito.drools.core.config.AbstractRuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.rules.RuleEventListenerConfig;

@org.springframework.stereotype.Component
class RuleConfig extends AbstractRuleConfig implements org.kie.kogito.rules.RuleConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public RuleConfig(
            List<RuleEventListenerConfig> ruleEventListenerConfigs,
            List<AgendaEventListener> agendaEventListeners,
            List<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        super(ruleEventListenerConfigs, agendaEventListeners, ruleRuntimeEventListeners);
    }
}
