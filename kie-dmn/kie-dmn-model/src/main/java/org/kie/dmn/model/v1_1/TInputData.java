package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InputData;

public class TInputData extends TDRGElement implements InputData {

    private InformationItem variable;

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem value) {
        this.variable = value;
    }

}
