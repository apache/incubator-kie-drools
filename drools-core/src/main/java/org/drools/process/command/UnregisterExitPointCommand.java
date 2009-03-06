package org.drools.process.command;

import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;

public class UnregisterExitPointCommand
    implements
    Command<Object> {

    private String name;

    public UnregisterExitPointCommand(String name) {
        this.name = name;
    }

    public Object execute(ReteooWorkingMemory session) {
        ReteooStatefulSession reteooStatefulSession = (ReteooStatefulSession) session;

        reteooStatefulSession.unregisterExitPoint( name );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.unregisterExitPoint( " + name + " );";
    }
}
