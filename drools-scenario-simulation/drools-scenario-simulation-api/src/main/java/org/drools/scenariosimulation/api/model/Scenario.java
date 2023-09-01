package org.drools.scenariosimulation.api.model;

import static java.util.stream.Collectors.toList;

/**
 * Scenario contains description and values to test in the defined scenario
 */
public class Scenario extends AbstractScesimData {

    @Override
    Scenario cloneInstance() {
        Scenario cloned = new Scenario();
        cloned.factMappingValues.addAll(factMappingValues.stream().map(FactMappingValue::cloneFactMappingValue).collect(toList()));
        return cloned;
    }
}