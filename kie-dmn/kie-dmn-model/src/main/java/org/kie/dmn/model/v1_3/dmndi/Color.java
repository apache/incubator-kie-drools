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
