package org.drools.process.command;

import java.util.Collection;

import org.drools.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandlesCommand
    implements
    Command<Collection< FactHandle >> {

    private ObjectFilter filter = null;

    public GetFactHandlesCommand() {
    }

    public GetFactHandlesCommand(ObjectFilter filter) {
        this.filter = filter;
    }

    public Collection< FactHandle > execute(ReteooWorkingMemory session) {
        ReteooStatefulSession reteooStatefulSession = (ReteooStatefulSession) session;

        if ( filter != null ) {
            return new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(),
                                           null,
                                           ObjectStoreWrapper.FACT_HANDLE );
        } else {
            return new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(),
                                           filter,
                                           ObjectStoreWrapper.FACT_HANDLE );
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
