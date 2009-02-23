package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.runtime.Globals;

public class GetGlobalsCommand
    implements
    Command<Globals> {

    public Globals execute(StatefulSession session) {
        return (Globals) session.getGlobalResolver();
    }

    public String toString() {
        return "session.getGlobalResolver()";
    }
}
