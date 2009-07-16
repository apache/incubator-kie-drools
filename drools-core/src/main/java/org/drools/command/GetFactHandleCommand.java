package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandleCommand
    implements
    GenericCommand<FactHandle> {

    private Object object;

    public GetFactHandleCommand(Object object) {
        this.object = object;
    }

    public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getFactHandle( object );
    }

    public String toString() {
        return "ksession.getFactHandle( " + object + " );";
    }
}
