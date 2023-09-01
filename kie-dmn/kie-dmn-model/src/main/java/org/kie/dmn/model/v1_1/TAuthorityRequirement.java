package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;

public class TAuthorityRequirement extends KieDMNModelInstrumentedBase implements AuthorityRequirement, NotADMNElementInV11 {

    private DMNElementReference requiredDecision;
    private DMNElementReference requiredInput;
    private DMNElementReference requiredAuthority;

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

    @Override
    public DMNElementReference getRequiredAuthority() {
        return requiredAuthority;
    }

    @Override
    public void setRequiredAuthority(final DMNElementReference value) {
        this.requiredAuthority = value;
    }
}
