package org.kie.dmn.trisotech.model.api;

import org.kie.dmn.model.api.Expression;

public interface Conditional extends Expression {

    Expression getIf();

    Expression getThen();

    Expression getElse();

    void setIf(Expression expr);

    void setThen(Expression expr);

    void setElse(Expression expr);
}
