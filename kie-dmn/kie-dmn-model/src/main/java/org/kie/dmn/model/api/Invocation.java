package org.kie.dmn.model.api;

import java.util.List;

public interface Invocation extends Expression {

    Expression getExpression();

    void setExpression(Expression value);

    List<Binding> getBinding();

}
