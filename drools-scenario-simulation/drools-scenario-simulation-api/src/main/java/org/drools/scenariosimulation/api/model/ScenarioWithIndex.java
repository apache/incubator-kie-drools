package org.drools.scenariosimulation.api.model;

/**
 * Tuple with <code>Scenario</code> and its index
 */
public class ScenarioWithIndex extends ScesimDataWithIndex<Scenario> {

    public ScenarioWithIndex() {
        // CDI
    }

    public ScenarioWithIndex(int index, Scenario scenario) {
        super(index, scenario);
    }
}