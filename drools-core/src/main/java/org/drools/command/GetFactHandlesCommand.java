package org.drools.command;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandlesCommand
    implements
    GenericCommand<Collection<FactHandle>> {

    private ObjectFilter filter = null;

    public GetFactHandlesCommand() {
    }

    public GetFactHandlesCommand(ObjectFilter filter) {
        this.filter = filter;
    }

    public Collection<FactHandle> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();        

        if ( filter != null ) {
            return ksession.getFactHandles( this.filter );
        } else {
            return ksession.getFactHandles( this.filter );
        }
    }

    public String toString() {
        if ( filter != null ) {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), null, ObjectStoreWrapper.FACT_HANDLE )";
        } else {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), filter, ObjectStoreWrapper.FACT_HANDLE )";
        }
    }
}
