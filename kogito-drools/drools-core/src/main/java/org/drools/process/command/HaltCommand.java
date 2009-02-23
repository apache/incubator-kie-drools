package org.drools.process.command;

import org.drools.StatefulSession;

public class HaltCommand
    implements
    Command<Object> {

    public Object execute(StatefulSession session) {
        session.halt();
        return null;
    }

    public String toString() {
        return "session.halt();";
    }
}
