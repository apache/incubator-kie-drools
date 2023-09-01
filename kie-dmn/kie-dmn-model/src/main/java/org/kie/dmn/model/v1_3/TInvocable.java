package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.Invocable;


public class TInvocable extends TDRGElement implements Invocable {

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
