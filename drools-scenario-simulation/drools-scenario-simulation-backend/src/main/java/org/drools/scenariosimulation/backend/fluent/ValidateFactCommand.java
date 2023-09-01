package org.drools.scenariosimulation.backend.fluent;

import java.util.Collection;
import java.util.List;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class ValidateFactCommand implements ExecutableCommand<Void> {

    private final List<FactCheckerHandle> factToCheck;

    public ValidateFactCommand(List<FactCheckerHandle> factToCheck) {
        this.factToCheck = factToCheck;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);
        Collection<?> objects = ksession.getObjects(new ConditionFilter(factToCheck));
        if (!objects.isEmpty()) {
            factToCheck.forEach(fact -> fact.getScenarioResult().setResult(true));
        } else {
            factToCheck.forEach(fact -> fact.getScenarioResult().getFactMappingValue().setExceptionMessage("There is no instance which satisfies the expected conditions"));
        }
        return null;
    }
}
