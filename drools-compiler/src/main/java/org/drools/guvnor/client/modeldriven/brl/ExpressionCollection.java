package org.drools.guvnor.client.modeldriven.brl;

public class ExpressionCollection extends ExpressionPart {

	@SuppressWarnings("unused")
	private ExpressionCollection() {
	}

	public ExpressionCollection(String name, String classType,
			String genericType, String parametricType) {
		super(name, classType, genericType, parametricType);
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

}
