package org.drools.scenariosimulation.backend.fluent;

import java.util.function.Function;

import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;

public class FactCheckerHandle {

    private final Class<?> clazz;
    private final Function<Object, ValueWrapper> checkFuction;
    private final ScenarioResult scenarioResult;

    public FactCheckerHandle(Class<?> clazz, Function<Object, ValueWrapper> checkFuction, ScenarioResult scenarioResult) {
        this.clazz = clazz;
        this.checkFuction = checkFuction;
        this.scenarioResult = scenarioResult;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Function<Object, ValueWrapper> getCheckFuction() {
        return checkFuction;
    }

    public ScenarioResult getScenarioResult() {
        return scenarioResult;
    }
}
