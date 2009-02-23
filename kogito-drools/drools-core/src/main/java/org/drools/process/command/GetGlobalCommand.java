package org.drools.process.command;

import org.drools.StatefulSession;

public class GetGlobalCommand
    implements
    Command<Object> {

    private String identifier;

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public Object execute(StatefulSession session) {
        return session.getGlobal( identifier );
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
