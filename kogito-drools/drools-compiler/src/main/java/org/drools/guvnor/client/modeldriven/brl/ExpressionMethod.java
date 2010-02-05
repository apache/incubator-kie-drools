package org.drools.guvnor.client.modeldriven.brl;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionMethod extends ExpressionPart {

	private Map<String, ExpressionFormLine> params = new LinkedHashMap<String, ExpressionFormLine>();

	protected ExpressionMethod() {}
	
	public ExpressionMethod(String methodName, String returnClassType, String returnGenericType) {
		super(methodName, returnClassType, returnGenericType);
	}

	public ExpressionMethod(String name, String classType, String genericType,
			String parametricType) {
		super(name, classType, genericType, parametricType);
	}

	public Map<String, ExpressionFormLine> getParams() {
		return params;
	}

	public void setParams(Map<String, ExpressionFormLine> params) {
		this.params.putAll(params);
	}
	
	public void putParam(String name, ExpressionFormLine expression) {
		this.params.put(name, expression);
	}

	protected String paramsToString() {
		if (params.isEmpty()) {
			return "";
		}
		String sep = ", ";
		StringBuilder s = new StringBuilder();
		for (ExpressionFormLine expr : params.values()) {
			s.append(sep).append(expr.getText());
		}
		return s.substring(sep.length());
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
