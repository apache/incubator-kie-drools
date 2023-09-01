package org.kie.dmn.model.api;

public interface KnowledgeRequirement extends DMNElement {

    DMNElementReference getRequiredKnowledge();

    void setRequiredKnowledge(DMNElementReference value);

}
