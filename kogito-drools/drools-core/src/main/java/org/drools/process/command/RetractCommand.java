package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class RetractCommand
    implements
    Command<Object> {

    private FactHandle handle;

    public RetractCommand(FactHandle handle) {
        this.handle = handle;
    }

    public Object execute(ReteooWorkingMemory session) {
        session.retract( handle );
        return null;
    }
    
    public FactHandle getFactHandle() {
        return this.handle;
    }

    public String toString() {
        return "session.retract( " + handle + " );";
    }
}
