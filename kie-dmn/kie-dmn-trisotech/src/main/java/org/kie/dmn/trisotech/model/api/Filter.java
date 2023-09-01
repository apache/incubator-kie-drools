package org.kie.dmn.trisotech.model.api;

import org.kie.dmn.model.api.Expression;

public interface Filter extends Expression {

    Expression getIn();

    Expression getMatch();

    void setIn(Expression expr);

    void setMatch(Expression expr);

}
