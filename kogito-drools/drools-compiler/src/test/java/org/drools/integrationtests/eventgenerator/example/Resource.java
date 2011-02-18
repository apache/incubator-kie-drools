/**
 * 
 */
package org.drools.integrationtests.eventgenerator.example;

import java.util.Calendar;

/**
 * @author Matthias Groch
 *
 */
public class Resource {

    public static final String[] OPERATIONAL_STATUS_VALUES = {"RED", "YELLOW", "GREEN"};

    public static final int STATUS_RED = 0;
    public static final int STATUS_YELLOW = 1;
    public static final int STATUS_GREEN = 2;

    private static int idCounter = 0;

    private String id;
    private String name;
    private double pressure, temperature;
    private Calendar lastHeartBeat;
    private Status opStatus;

    public Resource(String name) {
        this.id = String.valueOf(idCounter++);
        this.name = name;
        this.pressure = 0;
        this.temperature = 0;
        this.lastHeartBeat = null;
        this.opStatus = new Status(Status.OPERATIONAL, this.id, OPERATIONAL_STATUS_VALUES, STATUS_RED);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the resource type
     */
    public String getName() {
        return name;
    }

    /**
     * @param type the resource type to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the opStatus
     */
    public Status getOpStatus() {
        return opStatus;
    }

    /**
     * @param opStatus the opStatus to set
     */
    public void setOpStatus(Status status) {
        this.opStatus = status;
    }

    /**
     * @return the pressure
     */
    public double getPressure() {
        return pressure;
    }

    /**
     * @param pressure the pressure to set
     */
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    /**
     * @return the temperature
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * @return the lastHeartBeat
     */
    public Calendar getLastHeartBeat() {
        return lastHeartBeat;
    }

    /**
     * @param lastHeartBeat the lastHeartBeat to set
     */
    public void setLastHeartBeat(Calendar lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

}
