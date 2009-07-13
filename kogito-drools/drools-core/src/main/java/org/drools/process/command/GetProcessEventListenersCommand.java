package org.drools.process.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.process.ProcessEventListener;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class GetProcessEventListenersCommand
    implements
    GenericCommand<Collection<ProcessEventListener> > {

    public Collection<ProcessEventListener> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getProcessEventListeners();
    }

    public String toString() {
        return "session.getProcessEventListeners();";
    }
}
