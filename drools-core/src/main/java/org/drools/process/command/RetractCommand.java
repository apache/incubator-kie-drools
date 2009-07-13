package org.drools.process.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class RetractCommand
    implements
    GenericCommand<Object> {

    private FactHandle handle;

    public RetractCommand(FactHandle handle) {
        this.handle = handle;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.retract( handle );
        return null;
    }
    
    public FactHandle getFactHandle() {
        return this.handle;
    }

    public String toString() {
        return "session.retract( " + handle + " );";
    }
}
