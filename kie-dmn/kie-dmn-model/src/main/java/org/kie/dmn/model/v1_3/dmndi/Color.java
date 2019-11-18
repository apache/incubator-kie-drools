/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.model.v1_3.dmndi;

import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;

public class Color extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.Color {

    protected int red;
    protected int green;
    protected int blue;

    /**
     * Gets the value of the red property.
     * 
     */
    public int getRed() {
        return red;
    }

    /**
     * Sets the value of the red property.
     * 
     */
    public void setRed(int value) {
        this.red = value;
    }

    /**
     * Gets the value of the green property.
     * 
     */
    public int getGreen() {
        return green;
    }

    /**
     * Sets the value of the green property.
     * 
     */
    public void setGreen(int value) {
        this.green = value;
    }

    /**
     * Gets the value of the blue property.
     * 
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Sets the value of the blue property.
     * 
     */
    public void setBlue(int value) {
        this.blue = value;
    }

}
