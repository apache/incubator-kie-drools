package org.kie.dmn.model.api;

import java.util.List;

public interface Decision extends DRGElement {

    String getQuestion();

    void setQuestion(String value);

    String getAllowedAnswers();

    void setAllowedAnswers(String value);

    InformationItem getVariable();

    void setVariable(InformationItem value);

    List<InformationRequirement> getInformationRequirement();

    List<KnowledgeRequirement> getKnowledgeRequirement();

    List<AuthorityRequirement> getAuthorityRequirement();

    List<DMNElementReference> getSupportedObjective();

    List<DMNElementReference> getImpactedPerformanceIndicator();

    List<DMNElementReference> getDecisionMaker();

    List<DMNElementReference> getDecisionOwner();

    List<DMNElementReference> getUsingProcess();

    List<DMNElementReference> getUsingTask();

    Expression getExpression();

    void setExpression(Expression value);

    String toString();

}
