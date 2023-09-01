package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InputData;

public class TInputData extends TDRGElement implements InputData {

    protected InformationItem variable;

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(InformationItem value) {
        this.variable = value;
    }

}
