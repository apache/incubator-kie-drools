package org.drools.guvnor.client.modeldriven.brl;

public abstract class ExpressionPart implements IPattern, IAction, ExpressionVisitable {
	private ExpressionPart prev;
	private ExpressionPart next;
	private String name;
	private String classType;
	private String genericType;
	private String parametricType;

	protected ExpressionPart() {
	}
	
	public ExpressionPart(String name, String classType, String genericType) {
		this.name = name;
		this.classType = classType;
		this.genericType = genericType;
	}
	
	public ExpressionPart(String name, String classType, String genericType, String parametricType ) {
		this(name, classType, genericType);
		this.parametricType = parametricType;
	}

	public String getName() {
		return name;
	}
	
	public final String getClassType() {
		return classType;
	}
	
	public final String getGenericType() {
		return genericType;
	}
	
	public String getParametricType() {
		return parametricType;
	}

	public ExpressionPart getPrevious() {
		return prev;
	}

	public void setPrevious(ExpressionPart prev) {
		this.prev = prev;
		if (prev != null) {
			prev.next = this;
		}
	}

	public ExpressionPart getNext() {
		return next;
	}

	public void setNext(ExpressionPart next) {
		this.next = next;
		if (next != null) {
			next.prev = this;
		}
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
