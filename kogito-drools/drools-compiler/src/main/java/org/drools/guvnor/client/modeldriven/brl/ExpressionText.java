package org.drools.guvnor.client.modeldriven.brl;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;

public class ExpressionText extends ExpressionPart {

	protected ExpressionText() {}

	public ExpressionText(String name, String classType, String genericType) {
		super(name, classType, genericType);
	}

	public ExpressionText(String name) {
		super(name, "java.lang.String", SuggestionCompletionEngine.TYPE_STRING);
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

}
