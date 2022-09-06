
package it.pkg;

import java.util.HashSet;
import java.util.Set;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class MeasurementUnit implements RuleUnitData {

    private final DataStore<Measurement> measurements;
    private final Set<String> controlSet = new HashSet<>();

    public MeasurementUnit() {
        this(DataSource.createStore());
    }

    public MeasurementUnit(DataStore<Measurement> measurements) {
        this.measurements = measurements;
    }

    public DataStore<Measurement> getMeasurements() {
        return measurements;
    }

    public Set<String> getControlSet() {
        return controlSet;
    }
}
