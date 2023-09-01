package org.kie.dmn.model.api;

import java.util.List;

public interface BusinessKnowledgeModel extends Invocable {

    FunctionDefinition getEncapsulatedLogic();

    void setEncapsulatedLogic(FunctionDefinition value);

    List<KnowledgeRequirement> getKnowledgeRequirement();

    List<AuthorityRequirement> getAuthorityRequirement();

}
