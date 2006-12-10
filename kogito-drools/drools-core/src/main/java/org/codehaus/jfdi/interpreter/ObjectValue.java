package org.codehaus.jfdi.interpreter;

public class ObjectValue implements ValueHandler {
	
	private Object value;

	public ObjectValue(Object value) {
		this.value = value;
	}

	public void setValue(Object value) {
		throw new NotAnLValueException( value );
	}

	public boolean isLocal() {
		return true;
	}

	public boolean isFinal() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	public Class getType() {
		return value.getClass();
	}

	public Object getValue() {
		return value;
	}

}
