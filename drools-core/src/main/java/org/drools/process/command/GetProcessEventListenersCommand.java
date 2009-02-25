package org.drools.process.command;

import java.util.Collection;

import org.drools.StatefulSession;
import org.drools.event.RuleFlowEventListener;

public class GetProcessEventListenersCommand
    implements
    Command<Collection<RuleFlowEventListener>> {

    public Collection<RuleFlowEventListener> execute(StatefulSession session) {
        return session.getRuleFlowEventListeners();
    }

    public String toString() {
        return "session.getRuleFlowEventListeners();";
    }
}
