package org.kie.dmn.model.v1x;

public interface InformationRequirement extends DMNElement {

    DMNElementReference getRequiredDecision();

    void setRequiredDecision(DMNElementReference value);

    DMNElementReference getRequiredInput();

    void setRequiredInput(DMNElementReference value);

}
