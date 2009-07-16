package org.drools.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetAgendaEventListenersCommand
    implements
    GenericCommand<Collection<AgendaEventListener>> {

    public Collection<AgendaEventListener> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getAgendaEventListeners();
    }

    public String toString() {
        return "session.getAgendaEventListeners();";
    }
}
