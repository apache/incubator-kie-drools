package org.kie.dmn.model.v1x;

public interface Binding {

    InformationItem getParameter();

    void setParameter(InformationItem value);

    Expression getExpression();

    void setExpression(Expression value);

}
