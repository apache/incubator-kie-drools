package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.Calendars;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetCalendarsCommand
    implements
    GenericCommand<Calendars> {

    public Calendars execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return (Calendars) ksession.getCalendars();
    }

    public String toString() {
        return "session.getCalendars()";
    }
}
