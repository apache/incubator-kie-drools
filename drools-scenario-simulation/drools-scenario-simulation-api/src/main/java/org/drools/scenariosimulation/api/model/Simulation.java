package org.drools.scenariosimulation.api.model;

import java.util.List;

/**
 * Envelop class that wrap the definition of the simulation and the values of the scenarios
 */
public class Simulation extends AbstractScesimModel<Scenario> {

    public List<ScenarioWithIndex> getScenarioWithIndex() {
        return toScesimDataWithIndex(ScenarioWithIndex::new);
    }

    @Override
    public Scenario addData(int index) {
        if (index < 0 || index > scesimData.size()) {
            throw new IndexOutOfBoundsException(new StringBuilder().append("Index out of range ").append(index).toString());
        }
        Scenario scenario = new Scenario();
        scesimData.add(index, scenario);
        return scenario;
    }

    @Override
    public Simulation cloneModel() {
        Simulation toReturn = new Simulation();
        final List<FactMapping> originalFactMappings = this.scesimModelDescriptor.getUnmodifiableFactMappings();
        for (int i = 0; i < originalFactMappings.size(); i++) {
            final FactMapping originalFactMapping = originalFactMappings.get(i);
            toReturn.scesimModelDescriptor.addFactMapping(i, originalFactMapping);
        }
        this.scesimData.forEach(scenario -> toReturn.scesimData.add(scenario.cloneInstance()));
        return toReturn;
    }
}