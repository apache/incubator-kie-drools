package org.drools.command.runtime.rule;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class GetWorkingMemoryEntryPointsCommand
    implements
    GenericCommand<Collection< ? extends WorkingMemoryEntryPoint>> {

    public GetWorkingMemoryEntryPointsCommand() {
    }

    public Collection< ? extends WorkingMemoryEntryPoint> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getWorkingMemoryEntryPoints();
    }

    public String toString() {
        return "session.getWorkingMemoryEntryPoints( );";
    }
}
