package org.kie.dmn.model.v1_2.dmndi;

import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;

public class Point extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.Point {

    protected double x;
    protected double y;

    /**
     * Gets the value of the x property.
     * 
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     */
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     */
    public void setY(double value) {
        this.y = value;
    }

}
