package org.kie.dmn.model.v1_4;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TBusinessKnowledgeModel extends TInvocable implements BusinessKnowledgeModel {

    protected FunctionDefinition encapsulatedLogic;
    protected List<KnowledgeRequirement> knowledgeRequirement;
    protected List<AuthorityRequirement> authorityRequirement;

    @Override
    public FunctionDefinition getEncapsulatedLogic() {
        return encapsulatedLogic;
    }

    @Override
    public void setEncapsulatedLogic(FunctionDefinition value) {
        this.encapsulatedLogic = value;
    }

    @Override
    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if (knowledgeRequirement == null) {
            knowledgeRequirement = new ArrayList<>();
        }
        return this.knowledgeRequirement;
    }

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if (authorityRequirement == null) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

}
