package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.time.SessionClock;

public class GetSessionClockCommand
    implements
    Command<SessionClock> {

    public SessionClock execute(ReteooWorkingMemory session) {
        return session.getSessionClock();
    }

    public String toString() {
        return "session.getSessionClock();";
    }
}
