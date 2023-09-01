package org.kie.dmn.model.v1_3.dmndi;

import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;

public class Dimension extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.Dimension {

    protected double width;
    protected double height;

    /**
     * Gets the value of the width property.
     * 
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(double value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(double value) {
        this.height = value;
    }

}
