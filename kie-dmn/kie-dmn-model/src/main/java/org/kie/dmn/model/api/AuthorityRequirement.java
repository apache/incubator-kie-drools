package org.kie.dmn.model.api;

public interface AuthorityRequirement extends DMNElement {

    DMNElementReference getRequiredDecision();

    void setRequiredDecision(DMNElementReference value);

    DMNElementReference getRequiredInput();

    void setRequiredInput(DMNElementReference value);

    DMNElementReference getRequiredAuthority();

    void setRequiredAuthority(DMNElementReference value);

}
