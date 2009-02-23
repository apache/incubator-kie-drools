package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.time.SessionClock;

public class GetSessionClockCommand
    implements
    Command<SessionClock> {

    public SessionClock execute(StatefulSession session) {
        return session.getSessionClock();
    }

    public String toString() {
        return "session.getSessionClock();";
    }
}
