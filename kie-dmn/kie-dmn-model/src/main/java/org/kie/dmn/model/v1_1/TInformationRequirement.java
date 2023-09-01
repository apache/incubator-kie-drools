package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.InformationRequirement;

public class TInformationRequirement extends KieDMNModelInstrumentedBase implements InformationRequirement, NotADMNElementInV11 {

    private DMNElementReference requiredDecision;
    private DMNElementReference requiredInput;

    @Override
    public DMNElementReference getRequiredDecision() {
        return requiredDecision;
    }

    @Override
    public void setRequiredDecision(final DMNElementReference value) {
        this.requiredDecision = value;
    }

    @Override
    public DMNElementReference getRequiredInput() {
        return requiredInput;
    }

    @Override
    public void setRequiredInput(final DMNElementReference value) {
        this.requiredInput = value;
    }

}
