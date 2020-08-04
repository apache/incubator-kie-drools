package $Package$;

import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.rules.units.impl.AbstractRuleUnit;

public class $Name$ extends AbstractRuleUnit<$ModelName$> {

    public $Name$(org.kie.kogito.Application app) {
        super(app);
    }

    public $InstanceName$ internalCreateInstance($ModelName$ value) {
        return new $InstanceName$( this, value, createLegacySession());
    }

    private KieSession createLegacySession() {
        KieSession ks = app.ruleUnits().ruleRuntimeBuilder().newKieSession( $ModelClass$ );
        ((org.drools.core.impl.KogitoStatefulKnowledgeSessionImpl)ks).setApplication( app );
        if (app.config() != null && app.config().rule() != null) {
            RuleEventListenerConfig ruleEventListenerConfig = app.config().rule().ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(ks::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(ks::addEventListener);
        }
        return ks;
    }
}
