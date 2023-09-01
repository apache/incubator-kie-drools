package org.drools.commands;

import java.util.Objects;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class SetActiveAgendaGroup implements ExecutableCommand<Void> {

    private final String agendaGroup;

    public SetActiveAgendaGroup(String agendaGroup) {
        this.agendaGroup = Objects.requireNonNull(agendaGroup);
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);

        ksession.getAgenda().getAgendaGroup(agendaGroup).setFocus();

        return null;
    }

    @Override
    public String toString() {
        return "SetActiveAgendaGroup{" +
                "agendaGroup='" + agendaGroup + '\'' +
                '}';
    }
}
