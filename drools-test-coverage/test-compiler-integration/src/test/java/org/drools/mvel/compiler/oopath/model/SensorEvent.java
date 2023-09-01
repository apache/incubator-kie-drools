package org.drools.mvel.compiler.oopath.model;

public class SensorEvent {
    private Sensor sensor;
    private double value;

    public SensorEvent(Sensor sensor, double value) {
        this.sensor = sensor;
        this.value = value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
