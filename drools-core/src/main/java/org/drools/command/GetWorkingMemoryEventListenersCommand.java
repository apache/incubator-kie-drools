package org.drools.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Globals;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetWorkingMemoryEventListenersCommand
    implements
    GenericCommand<Collection<WorkingMemoryEventListener>> {

    public Collection<WorkingMemoryEventListener> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getWorkingMemoryEventListeners();
    }

    public String toString() {
        return "session.getWorkingMemoryEventListeners();";
    }

}
