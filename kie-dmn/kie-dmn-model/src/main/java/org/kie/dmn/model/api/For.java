package org.kie.dmn.model.api;

public interface For extends Iterator {
	
	ChildExpression getReturn();
	
	void setReturn(ChildExpression value);
	
}
