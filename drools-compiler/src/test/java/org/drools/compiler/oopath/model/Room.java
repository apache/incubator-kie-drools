/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.oopath.model;

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
