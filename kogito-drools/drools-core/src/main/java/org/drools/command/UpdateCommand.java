package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class UpdateCommand
    implements
    GenericCommand<Object> {

    private FactHandle handle;
    private Object     object;

    public UpdateCommand(FactHandle handle,
                         Object object) {
        this.handle = handle;
        this.object = object;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        ksession.update( handle,
                        object );
        return null;
    }

    public String toString() {
        return "session.update( " + handle + ", " + object + " );";
    }
}
