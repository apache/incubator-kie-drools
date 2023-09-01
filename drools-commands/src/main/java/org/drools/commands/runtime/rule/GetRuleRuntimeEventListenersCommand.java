package org.drools.commands.runtime.rule;

import java.util.Collection;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetRuleRuntimeEventListenersCommand
    implements
    ExecutableCommand<Collection<RuleRuntimeEventListener>> {

    public Collection<RuleRuntimeEventListener> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getRuleRuntimeEventListeners();
    }

    public String toString() {
        return "session.getRuleRuntimeEventListeners();";
    }
}
