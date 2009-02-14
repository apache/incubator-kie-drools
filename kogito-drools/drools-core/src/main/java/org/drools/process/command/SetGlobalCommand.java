package org.drools.process.command;

import org.drools.StatefulSession;

public class SetGlobalCommand
    implements
    Command<Void> {

    private String identifier;
    private Object object;

    public SetGlobalCommand(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public Void execute(StatefulSession session) {
        session.setGlobal( identifier,
                           object );
        return null;
    }

    public String toString() {
        return "session.setGlobal(" + identifier + ", " + object + ");";
    }

}
