package org.kie.dmn.model.api;

public interface Quantified extends Iterator {
	
	ChildExpression getSatisfies();
	
	void setSatisfies(ChildExpression value);
	
}
