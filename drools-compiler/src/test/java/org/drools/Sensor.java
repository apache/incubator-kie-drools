package org.drools;

public class Sensor {
    private int temperature;
    private int pressure;
    
    public Sensor(int temp, int press) {
        this.temperature = temp;
        this.pressure = press;
    }

    /**
     * @return the pressure
     */
    public int getPressure() {
        return pressure;
    }

    /**
     * @param pressure the pressure to set
     */
    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    /**
     * @return the temperature
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    
    public String toString() {
        return "Sensor [ temperature = "+this.temperature+", pressure = "+this.pressure+" ]";
    }

}
