package $Package$;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.impl.EnvironmentImpl;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSession;

public class $Name$ extends org.kie.kogito.rules.impl.AbstractRuleUnit<$ModelName$> {

    public $Name$() {
        this(new $Application$());
    }

    public $Name$(org.kie.kogito.Application app) {
        super(app);
    }

    public $InstanceName$ createInstance($ModelName$ value) {
        return new $InstanceName$(
                this,
                value,
                createLegacySession());
    }

    private org.kie.api.runtime.KieSession createLegacySession() {
        if (app.config() != null && app.config().rule() != null) {
            org.kie.kogito.rules.RuleConfig ruleCfg = app.config().rule();

            KieBaseConfiguration kieBaseConfiguration =
                    new RuleBaseConfiguration();

            kieBaseConfiguration.setOption(ruleCfg.eventProcessingMode());

            org.drools.core.impl.InternalKnowledgeBase kb =
                    org.drools.modelcompiler.builder.KieBaseBuilder.createKieBaseFromModel(
                            new $RuleModelName$(), kieBaseConfiguration);

            SessionConfigurationImpl sessionConfiguration = new SessionConfigurationImpl();
            sessionConfiguration.setOption(ruleCfg.clockType());

            KieSession ks = kb.newKieSession(sessionConfiguration, new EnvironmentImpl());

            org.kie.kogito.rules.RuleEventListenerConfig ruleEventListenerConfig = ruleCfg.ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(ks::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(ks::addEventListener);

            return ks;
        } else {
            return org.drools.modelcompiler.builder.KieBaseBuilder.createKieBaseFromModel(
                    new $RuleModelName$()).newKieSession();
        }
    }
}
