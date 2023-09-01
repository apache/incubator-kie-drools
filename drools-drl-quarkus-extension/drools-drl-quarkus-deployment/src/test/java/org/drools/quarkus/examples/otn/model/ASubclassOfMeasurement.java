package org.drools.quarkus.examples.otn.model;

public class ASubclassOfMeasurement extends Measurement {

    public ASubclassOfMeasurement(String id, String val) {
        super(id, val);
    }

    @Override
    public String toString() {
        return "ASubclassOfMeasurement{ "+ super.toString() +"}";
    }
}
