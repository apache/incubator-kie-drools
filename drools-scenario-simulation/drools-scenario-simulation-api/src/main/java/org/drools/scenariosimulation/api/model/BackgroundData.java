package org.drools.scenariosimulation.api.model;

import static java.util.stream.Collectors.toList;

/**
 * BackgroundData contains values to use inside simulation
 */
public class BackgroundData extends AbstractScesimData {

    @Override
    BackgroundData cloneInstance() {
        BackgroundData cloned = new BackgroundData();
        cloned.factMappingValues.addAll(factMappingValues.stream().map(FactMappingValue::cloneFactMappingValue).collect(toList()));
        return cloned;
    }
}