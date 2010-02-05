package org.drools.guvnor.client.modeldriven.brl;


public class ExpressionField extends ExpressionPart {
	@SuppressWarnings("unused")
	private ExpressionField() {}
	
	public ExpressionField(String fieldName, String fieldClassType, String fieldGenericType) {
		super(fieldName, fieldClassType, fieldGenericType);
	}

	public ExpressionField(String fieldName, String fieldClassType, String fieldGenericType, String parametricType) {
		super(fieldName, fieldClassType, fieldGenericType, parametricType);
	}
	
//	@Override
//	public String getText() {
//		return "." + getName() + (getNext() == null ? "" : getNext().getText());
//	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
