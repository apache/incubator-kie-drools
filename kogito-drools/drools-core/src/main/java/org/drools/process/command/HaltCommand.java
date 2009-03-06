package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;

public class HaltCommand
    implements
    Command<Object> {

    public Object execute(ReteooWorkingMemory session) {
        session.halt();
        return null;
    }

    public String toString() {
        return "session.halt();";
    }
}
