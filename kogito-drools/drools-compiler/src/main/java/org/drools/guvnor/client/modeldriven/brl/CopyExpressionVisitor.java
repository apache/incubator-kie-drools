package org.drools.guvnor.client.modeldriven.brl;

import java.util.HashMap;
import java.util.Map;

public class CopyExpressionVisitor implements ExpressionVisitor {

	private ExpressionPart root;
	private ExpressionPart curr;
	
	public ExpressionPart copy(ExpressionPart part) {
		root = null;
		curr = null;
		part.accept(this);
		return root;
	}
	
	public void visit(ExpressionPart part) {
		throw new RuntimeException("can't copy an abstract class: " + ExpressionPart.class.getName());
	}

	public void visit(ExpressionField part) {
		add(new ExpressionField(part.getName(), part.getClassType(), part.getGenericType(), part.getParametricType()));
		moveNext(part);
	}

	public void visit(ExpressionMethod part) {
		ExpressionMethod method = new ExpressionMethod(part.getName(), part.getClassType(), part.getGenericType(), part.getParametricType());
		copyMethodParams(part, method);
		add(method);
		moveNext(part);
	}

	public void visit(ExpressionVariable part) {
		add(new ExpressionVariable(part.getFact()));
		moveNext(part);
	}

	public void visit(ExpressionCollection part) {
		add(new ExpressionCollection(part.getName(), part.getClassType(), part.getGenericType(), part.getParametricType()));
		moveNext(part);
	}

	public void visit(ExpressionCollectionIndex part) {
		ExpressionCollectionIndex method = new ExpressionCollectionIndex(part.getName(), part.getClassType(), part.getGenericType(), part.getParametricType());
		copyMethodParams(part, method);
		add(method);
		moveNext(part);
	}

	public void visit(ExpressionText part) {
		add(new ExpressionText(part.getName(), part.getClassType(), part.getGenericType()));
		moveNext(part);
	}

	public void visit(ExpressionGlobalVariable part) {
		add(new ExpressionGlobalVariable(part.getName(), part.getClassType(), part.getGenericType(), part.getParametricType()));
		moveNext(part);
		
	}
	
	private void copyMethodParams(ExpressionMethod part, ExpressionMethod method) {
		Map<String, ExpressionFormLine> params = new HashMap<String, ExpressionFormLine>();
		for (Map.Entry<String, ExpressionFormLine> entry : part.getParams().entrySet()) {
			params.put(entry.getKey(), new ExpressionFormLine(entry.getValue()));
		}
		method.setParams(params);
	}
	
	private void moveNext(ExpressionPart ep) {
		if (ep.getNext() != null) {
			ep.getNext().accept(this);
		}
	}

	private void add(ExpressionPart p) {
		if (root == null) {
			root = p;
			curr = p;
		} else {
			curr.setNext(p);
			curr = p;
		}
	}


}
