package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;

public class GetEnvironmentCommand
    implements
    Command<Environment> {

    public Environment execute(ReteooWorkingMemory session) {
        return session.getEnvironment();
    }

    public String toString() {
        return "session.getEnvironment();";
    }

}
