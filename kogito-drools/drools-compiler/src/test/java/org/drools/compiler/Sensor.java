/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler;

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
