package $Package$;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.impl.EnvironmentImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSession;
import org.kie.api.KieBase;

public class $Name$ extends org.kie.kogito.rules.impl.AbstractRuleUnit<$ModelName$> {

    private final KieBase kbase;

    public $Name$() {
        this(new $Application$());
    }

    public $Name$(org.kie.kogito.Application app) {
        super(app);
        this.kbase = createKieBase();
    }

    public $InstanceName$ createInstance($ModelName$ value) {
        return new $InstanceName$(
                this,
                value,
                createLegacySession());
    }

    private KieBase createKieBase() {
        return app.ruleUnits().ruleRuntimeBuilder().getKieBase( $ModelClass$ );
    }

    private org.kie.api.runtime.KieSession createLegacySession() {
        if (app.config() != null && app.config().rule() != null) {
            org.kie.kogito.rules.RuleConfig ruleCfg = app.config().rule();
            SessionConfigurationImpl sessionConfiguration = new SessionConfigurationImpl();
            sessionConfiguration.setOption(ruleCfg.clockType());

            KieSession ks = kbase.newKieSession(sessionConfiguration, new EnvironmentImpl());

            org.kie.kogito.rules.RuleEventListenerConfig ruleEventListenerConfig = ruleCfg.ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(ks::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(ks::addEventListener);

            return ks;
        } else {
            return kbase.newKieSession();
        }
    }
}
