package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;

public class TAuthorityRequirement extends TDMNElement implements AuthorityRequirement {

    protected DMNElementReference requiredDecision;
    protected DMNElementReference requiredInput;
    protected DMNElementReference requiredAuthority;

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

    @Override
    public DMNElementReference getRequiredAuthority() {
        return requiredAuthority;
    }

    @Override
    public void setRequiredAuthority(DMNElementReference value) {
        this.requiredAuthority = value;
    }

}
