package org.drools.guvnor.client.modeldriven.brl;

public class ExpressionGlobalVariable extends ExpressionPart {

	protected ExpressionGlobalVariable() {
	}

	public ExpressionGlobalVariable(String name, String classType,
			String genericType) {
		super(name, classType, genericType);
	}

	public ExpressionGlobalVariable(String name, String classType,
			String genericType, String parametricType) {
		super(name, classType, genericType, parametricType);
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
