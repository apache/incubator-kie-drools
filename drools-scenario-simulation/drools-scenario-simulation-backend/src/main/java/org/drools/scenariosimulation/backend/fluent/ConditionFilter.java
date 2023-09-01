package org.drools.scenariosimulation.backend.fluent;

import java.util.List;

import org.kie.api.runtime.ObjectFilter;

public class ConditionFilter implements ObjectFilter {

    private final List<FactCheckerHandle> factToCheck;

    public ConditionFilter(List<FactCheckerHandle> factToCheck) {
        this.factToCheck = factToCheck;
    }

    @Override
    public boolean accept(Object object) {
        return factToCheck.stream()
                .allMatch(factCheckerHandle ->
                                  factCheckerHandle.getClazz().isAssignableFrom(object.getClass()) &&
                                          factCheckerHandle.getCheckFuction().apply(object).isValid());
    }
}
