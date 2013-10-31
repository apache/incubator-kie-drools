package org.drools.core.command.runtime.rule;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

import java.util.Collection;

public class GetRuleRuntimeEventListenersCommand
    implements
    GenericCommand<Collection<RuleRuntimeEventListener>> {

    public Collection<RuleRuntimeEventListener> execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        return ksession.getRuleRuntimeEventListeners();
    }

    public String toString() {
        return "session.getRuleRuntimeEventListeners();";
    }
}
