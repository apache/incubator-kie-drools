package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetCalendarsCommand
    implements
    ExecutableCommand<Calendars> {

    public Calendars execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getCalendars();
    }

    public String toString() {
        return "session.getCalendars()";
    }
}
