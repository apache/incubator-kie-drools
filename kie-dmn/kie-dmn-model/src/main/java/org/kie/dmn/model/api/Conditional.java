package org.kie.dmn.model.api;

public interface Conditional extends Expression {
	
    ChildExpression getIf();

    ChildExpression getThen();

    ChildExpression getElse();

    void setIf(ChildExpression value);

    void setThen(ChildExpression value);

    void setElse(ChildExpression value);
	
}
