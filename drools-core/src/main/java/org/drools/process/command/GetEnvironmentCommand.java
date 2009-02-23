package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.runtime.Environment;

public class GetEnvironmentCommand
    implements
    Command<Environment> {

    public Environment execute(StatefulSession session) {
        return session.getEnvironment();
    }

    public String toString() {
        return "session.getEnvironment();";
    }

}
