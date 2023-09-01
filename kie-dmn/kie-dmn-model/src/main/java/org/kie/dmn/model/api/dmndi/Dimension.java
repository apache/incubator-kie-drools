package org.kie.dmn.model.api.dmndi;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface Dimension extends DMNModelInstrumentedBase {

    public double getWidth();

    public void setWidth(double value);

    public double getHeight();

    public void setHeight(double value);
}
