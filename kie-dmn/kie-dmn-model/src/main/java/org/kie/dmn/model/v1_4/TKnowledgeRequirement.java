package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TKnowledgeRequirement extends TDMNElement implements KnowledgeRequirement {

    protected DMNElementReference requiredKnowledge;

    @Override
    public DMNElementReference getRequiredKnowledge() {
        return requiredKnowledge;
    }

    @Override
    public void setRequiredKnowledge(DMNElementReference value) {
        this.requiredKnowledge = value;
    }

}
