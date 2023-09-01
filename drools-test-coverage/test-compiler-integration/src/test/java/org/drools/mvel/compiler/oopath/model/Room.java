package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.AbstractReactiveObject;

public class Room extends AbstractReactiveObject {
    private String name;

    private Sensor lightSensor = new Sensor();
    private Sensor temperatureSensor = new Sensor();

    private Appliance light = new Appliance();
    private Appliance heating = new Appliance();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyModification();
    }

    public Sensor getLightSensor() {
        return lightSensor;
    }

    public void setLightSensor(Sensor lightSensor) {
        this.lightSensor = lightSensor;
        notifyModification();
    }

    public Sensor getTemperatureSensor() {
        return temperatureSensor;
    }

    public void setTemperatureSensor(Sensor temperatureSensor) {
        this.temperatureSensor = temperatureSensor;
        notifyModification();
    }

    public Appliance getLight() {
        return light;
    }

    public void setLight(Appliance light) {
        this.light = light;
        notifyModification();
    }

    public Appliance getHeating() {
        return heating;
    }

    public void setHeating(Appliance heating) {
        this.heating = heating;
        notifyModification();
    }
}
