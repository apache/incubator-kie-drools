package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.ExitPoint;

public class RegisterExitPointCommand
    implements
    Command<Object> {

    private String    name;
    private ExitPoint exitPoint;

    public RegisterExitPointCommand(String name,
                                    ExitPoint exitPoint) {
        this.name = name;
        this.exitPoint = exitPoint;
    }

    public Object execute(StatefulSession session) {
        ReteooStatefulSession reteooStatefulSession = (ReteooStatefulSession) session;

        reteooStatefulSession.registerExitPoint( name,
                                                 exitPoint );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.registerExitPoint( " + name + ", " + exitPoint + " );";
    }
}
