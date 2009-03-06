package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Globals;

public class GetGlobalsCommand
    implements
    Command<Globals> {

    public Globals execute(ReteooWorkingMemory session) {
        return (Globals) session.getGlobalResolver();
    }

    public String toString() {
        return "session.getGlobalResolver()";
    }
}
