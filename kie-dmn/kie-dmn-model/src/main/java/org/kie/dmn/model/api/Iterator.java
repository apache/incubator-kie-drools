package org.kie.dmn.model.api;

public interface Iterator extends Expression {
	
	TypedChildExpression getIn();
	
	void setIn(TypedChildExpression value);
	
	String getIteratorVariable();
	
	void setIteratorVariable(String value);
	
}
