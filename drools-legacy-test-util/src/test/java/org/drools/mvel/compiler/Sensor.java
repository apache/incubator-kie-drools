package org.drools.mvel.compiler;

import java.io.Serializable;

public class Sensor implements Serializable {
    private int temperature;
    private int pressure;

    public Sensor() {

    }

    public Sensor(final int temp,
                  final int press) {
        this.temperature = temp;
        this.pressure = press;
    }

    public int getPressure() {
        return this.pressure;
    }
    public void setPressure(final int pressure) {
        this.pressure = pressure;
    }

    public int getTemperature() {
        return this.temperature;
    }
    public void setTemperature(final int temperature) {
        this.temperature = temperature;
    }

    public String toString() {
        return "Sensor [ temperature = " + this.temperature + ", pressure = " + this.pressure + " ]";
    }

}
