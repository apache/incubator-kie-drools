package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandleCommand
    implements
    Command<FactHandle> {

    private Object object;

    public GetFactHandleCommand(Object object) {
        this.object = object;
    }

    public FactHandle execute(ReteooWorkingMemory session) {
        session.getFactHandle( object );
        return null;
    }

    public String toString() {
        return "session.getFactHandle( " + object + " );";
    }
}
