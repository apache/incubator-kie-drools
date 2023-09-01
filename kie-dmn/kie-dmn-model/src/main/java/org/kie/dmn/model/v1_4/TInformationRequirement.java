package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.InformationRequirement;

public class TInformationRequirement extends TDMNElement implements InformationRequirement {

    protected DMNElementReference requiredDecision;
    protected DMNElementReference requiredInput;

    @Override
    public DMNElementReference getRequiredDecision() {
        return requiredDecision;
    }

    @Override
    public void setRequiredDecision(DMNElementReference value) {
        this.requiredDecision = value;
    }

    @Override
    public DMNElementReference getRequiredInput() {
        return requiredInput;
    }

    @Override
    public void setRequiredInput(DMNElementReference value) {
        this.requiredInput = value;
    }

}
