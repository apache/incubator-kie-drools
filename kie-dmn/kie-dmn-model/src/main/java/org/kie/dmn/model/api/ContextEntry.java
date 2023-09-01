package org.kie.dmn.model.api;

public interface ContextEntry extends DMNElement {

    InformationItem getVariable();

    void setVariable(InformationItem value);

    Expression getExpression();

    void setExpression(Expression value);

}
