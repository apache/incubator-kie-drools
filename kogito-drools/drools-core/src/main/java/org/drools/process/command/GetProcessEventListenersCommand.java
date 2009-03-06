package org.drools.process.command;

import java.util.Collection;

import org.drools.event.RuleFlowEventListener;
import org.drools.reteoo.ReteooWorkingMemory;

public class GetProcessEventListenersCommand
    implements
    Command<Collection<RuleFlowEventListener>> {

    public Collection<RuleFlowEventListener> execute(ReteooWorkingMemory session) {
        return session.getRuleFlowEventListeners();
    }

    public String toString() {
        return "session.getRuleFlowEventListeners();";
    }
}
