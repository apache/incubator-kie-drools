package $Package$;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.impl.EnvironmentImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.Application;
import org.kie.kogito.rules.RuleEventListenerConfig;

public class $Name$ extends org.kie.kogito.rules.impl.AbstractRuleUnit<$ModelName$> {

    public $Name$() {
        this(new $Application$());
    }

    public $Name$(org.kie.kogito.Application app) {
        super(app);
    }

    public $InstanceName$ internalCreateInstance($ModelName$ value) {
        return new $InstanceName$( this, value, createLegacySession());
    }

    private KieSession createLegacySession() {
        KieSession ks = app.ruleUnits().ruleRuntimeBuilder().newKieSession( $ModelClass$ );
        ((org.drools.core.impl.StatefulKnowledgeSessionImpl)ks).setApplication( app );
        if (app.config() != null && app.config().rule() != null) {
            RuleEventListenerConfig ruleEventListenerConfig = app.config().rule().ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(ks::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(ks::addEventListener);
        }
        return ks;
    }
}
