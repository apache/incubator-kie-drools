package org.kie.dmn.model.api;

public interface ChildExpression {
	
	Expression getExpression();
	
	void setExpression(Expression value);

    String getId();

    void setId(String value);

}
