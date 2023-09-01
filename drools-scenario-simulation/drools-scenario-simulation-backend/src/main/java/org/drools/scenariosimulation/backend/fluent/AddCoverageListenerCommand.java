package org.drools.scenariosimulation.backend.fluent;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class AddCoverageListenerCommand implements ExecutableCommand<Void> {

    private CoverageAgendaListener coverageAgendaListener;

    public AddCoverageListenerCommand(CoverageAgendaListener coverageAgendaListener) {
        this.coverageAgendaListener = coverageAgendaListener;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);
        ksession.addEventListener(coverageAgendaListener);
        return null;
    }
}
