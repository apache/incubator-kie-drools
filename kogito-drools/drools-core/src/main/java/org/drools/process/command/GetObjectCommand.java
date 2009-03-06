package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.FactHandle;

public class GetObjectCommand
    implements
    Command<Object> {

    private FactHandle factHandle;

    public GetObjectCommand(FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public Object execute(ReteooWorkingMemory session) {
        return session.getObject( factHandle );
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
