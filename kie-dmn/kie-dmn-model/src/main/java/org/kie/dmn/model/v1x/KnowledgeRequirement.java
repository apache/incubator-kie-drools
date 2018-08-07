package org.kie.dmn.model.v1x;

public interface KnowledgeRequirement extends DMNElement {

    DMNElementReference getRequiredKnowledge();

    void setRequiredKnowledge(DMNElementReference value);

}
