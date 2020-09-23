package $Package$;

import org.drools.core.ClockType;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.impl.EnvironmentImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.rules.units.impl.AbstractRuleUnit;

public class $Name$ extends AbstractRuleUnit<$ModelName$> {

    public $Name$(org.kie.kogito.Application app) {
        super($ModelName$.class.getCanonicalName(), app);
    }

    public $InstanceName$ internalCreateInstance($ModelName$ value) {
        return new $InstanceName$( this, value, createLegacySession());
    }

    private KieSession createLegacySession() {
        RuleBaseConfiguration ruleBaseConfig = new RuleBaseConfiguration();
        ruleBaseConfig.setEventProcessingMode($EventProcessingMode$);
        ruleBaseConfig.setSessionPoolSize($SessionPoolSize$);
        org.drools.core.impl.InternalKnowledgeBase kb =
                org.drools.modelcompiler.builder.KieBaseBuilder.createKieBaseFromModel(
                        new $RuleModelName$(), ruleBaseConfig);

        SessionConfigurationImpl sessionConfig = new SessionConfigurationImpl();
        sessionConfig.setClockType($ClockType$);

        KieSession ks = kb.newKieSession(sessionConfig, new EnvironmentImpl());
        ((org.drools.core.impl.KogitoStatefulKnowledgeSessionImpl)ks).setStateless( /*$IsStateful$*/ true );
        ((org.drools.core.impl.KogitoStatefulKnowledgeSessionImpl)ks).setApplication( app );

        org.kie.kogito.Config cfg = app.config();
        if (cfg != null) {
            RuleEventListenerConfig ruleEventListenerConfig = cfg.rule().ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(ks::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(ks::addEventListener);
        }
        return ks;
    }
}
