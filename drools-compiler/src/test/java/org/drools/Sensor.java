package org.drools;

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

    /**
     * @return the pressure
     */
    public int getPressure() {
        return this.pressure;
    }

    /**
     * @param pressure the pressure to set
     */
    public void setPressure(final int pressure) {
        this.pressure = pressure;
    }

    /**
     * @return the temperature
     */
    public int getTemperature() {
        return this.temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(final int temperature) {
        this.temperature = temperature;
    }

    public String toString() {
        return "Sensor [ temperature = " + this.temperature + ", pressure = " + this.pressure + " ]";
    }

}
