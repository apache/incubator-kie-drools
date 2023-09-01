package org.kie.dmn.model.api;

public interface InputClause extends DMNElement {

    LiteralExpression getInputExpression();

    void setInputExpression(LiteralExpression value);

    UnaryTests getInputValues();

    void setInputValues(UnaryTests value);

}
