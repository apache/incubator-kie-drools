package org.kie.dmn.model.api;

public interface Binding extends DMNModelInstrumentedBase {

    InformationItem getParameter();

    void setParameter(InformationItem value);

    Expression getExpression();

    void setExpression(Expression value);

}
