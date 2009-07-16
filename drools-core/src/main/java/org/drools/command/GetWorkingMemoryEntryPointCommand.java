package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;

public class GetWorkingMemoryEntryPointCommand
    implements
    GenericCommand<WorkingMemoryEntryPoint> {

    private String name;

    public GetWorkingMemoryEntryPointCommand(String name) {
        this.name = name;
    }

    public WorkingMemoryEntryPoint execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getWorkingMemoryEntryPoint( name );
    }

    public String toString() {
        return "session.getWorkingMemoryEntryPoint( " + name + " );";
    }
}
