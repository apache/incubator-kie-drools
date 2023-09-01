package org.kie.dmn.model.api.dmndi;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface Point extends DMNModelInstrumentedBase {

    public double getX();

    public void setX(double value);

    public double getY();

    public void setY(double value);
}
