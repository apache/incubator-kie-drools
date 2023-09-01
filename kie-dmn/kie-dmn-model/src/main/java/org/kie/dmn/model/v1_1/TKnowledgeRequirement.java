package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TKnowledgeRequirement extends KieDMNModelInstrumentedBase implements KnowledgeRequirement, NotADMNElementInV11 {

    private DMNElementReference requiredKnowledge;

    @Override
    public DMNElementReference getRequiredKnowledge() {
        return requiredKnowledge;
    }

    @Override
    public void setRequiredKnowledge(final DMNElementReference value) {
        this.requiredKnowledge = value;
    }

}
