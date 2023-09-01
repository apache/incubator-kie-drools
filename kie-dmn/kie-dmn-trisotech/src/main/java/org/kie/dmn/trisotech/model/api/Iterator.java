package org.kie.dmn.trisotech.model.api;

import org.kie.dmn.model.api.Expression;

public interface Iterator extends Expression {

    enum IteratorType {
        FOR,
        EVERY,
        SOME
    }

    String getVariable();

    Expression getIn();

    Expression getReturn();

    IteratorType getIteratorType();

    void setVariable(String var);

    void setIn(Expression expr);

    void setReturn(Expression expr);

    void setIteratorType(IteratorType type);

}
