package org.kie.dmn.model.api;

public interface Filter extends Expression {
	
	ChildExpression getIn();

	ChildExpression getMatch();

    void setIn(ChildExpression value);

    void setMatch(ChildExpression value);
	
}
