package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TBusinessKnowledgeModel extends TDRGElement implements BusinessKnowledgeModel {

    private FunctionDefinition encapsulatedLogic;
    private InformationItem variable;
    private List<KnowledgeRequirement> knowledgeRequirement;
    private List<AuthorityRequirement> authorityRequirement;

    @Override
    public FunctionDefinition getEncapsulatedLogic() {
        return encapsulatedLogic;
    }

    @Override
    public void setEncapsulatedLogic(final FunctionDefinition value) {
        this.encapsulatedLogic = value;
    }

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem value) {
        this.variable = value;
    }

    @Override
    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if ( knowledgeRequirement == null ) {
            knowledgeRequirement = new ArrayList<>();
        }
        return this.knowledgeRequirement;
    }

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if ( authorityRequirement == null ) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

}
