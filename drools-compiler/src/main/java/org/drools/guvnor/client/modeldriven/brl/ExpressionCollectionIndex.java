package org.drools.guvnor.client.modeldriven.brl;

public class ExpressionCollectionIndex extends ExpressionMethod {

	protected ExpressionCollectionIndex() {}
	
	public ExpressionCollectionIndex(String name, String classType,
			String genericType, String parametricType) {
		super(name, classType, genericType, parametricType);
	}

	public ExpressionCollectionIndex(String name, String returnClassType,
			String returnGenericType) {
		super(name, returnClassType, returnGenericType);
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}


}
