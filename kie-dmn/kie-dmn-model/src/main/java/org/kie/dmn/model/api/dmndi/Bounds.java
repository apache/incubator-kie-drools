package org.kie.dmn.model.api.dmndi;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface Bounds extends DMNModelInstrumentedBase {


    public double getX();

    public void setX(double value);

    public double getY();

    public void setY(double value);

    public double getWidth();

    public void setWidth(double value);

    public double getHeight();

    public void setHeight(double value);
}
