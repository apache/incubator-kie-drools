package org.drools.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionClock;
import org.kie.internal.command.RegistryContext;

public class GetSessionClockCommand
    implements
    ExecutableCommand<SessionClock> {

    public SessionClock execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);
        return ksession.getSessionClock();
    }

    public String toString() {
        return "session.getSessionClock();";
    }
}
