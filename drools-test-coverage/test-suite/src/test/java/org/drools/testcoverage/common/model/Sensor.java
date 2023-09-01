/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.testcoverage.common.model;

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
