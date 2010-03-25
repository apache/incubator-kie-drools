package org.drools.guvnor.client.modeldriven.brl;

import java.util.LinkedList;
import java.util.Map;

public class ExpressionFormLine implements IAction, IPattern, Cloneable {
	
    private LinkedList<ExpressionPart> parts = new LinkedList<ExpressionPart>() ;
    
    public ExpressionFormLine() {}

    public ExpressionFormLine(ExpressionPart part) {
    	appendPart(part);
    }
    
	public ExpressionFormLine(ExpressionFormLine other) {
		CopyExpressionVisitor copier = new CopyExpressionVisitor();
		
		for (ExpressionPart exp = copier.copy(other.getRootExpression()); exp != null; exp = exp.getNext()) {
			parts.add(exp);
		}
	}

	public String getText() {
		return new ToStringVisitor().buildString(getRootExpression());
	}
	
	public void appendPart(ExpressionPart part) {
		if (!parts.isEmpty()) {
			parts.getLast().setNext(part);
		}
		parts.add(part);
	}
	
	public void removeLast() {
		if (!parts.isEmpty()) {
			ExpressionPart last = parts.removeLast();
			if (last.getPrevious() != null) {
				last.getPrevious().setNext(null);
				last.setPrevious(null);
			}
		}
	}
	
	private ExpressionPart getPreviousPart() {
		return parts.getLast();
	}
	
	public String getPreviousType() {
		ExpressionPart last = getPreviousPart();
		return last.getPrevious() == null ? null : last.getPrevious().getClassType(); 
	}
	
	public String getClassType() {
		return parts.getLast().getClassType();
	}
	
	public String getGenericType() {
		return parts.getLast().getGenericType();
	}

	public String getParametricType() {
		return parts.getLast().getParametricType();
	}
	
	public boolean isEmpty() {
		return parts.isEmpty();
	}
	
	public String getCurrentName() {
		return parts.getLast().getName();
	}
	
	public String getPreviousName() {
		ExpressionPart previousPart = getPreviousPart();
		return previousPart == null ? null : previousPart.getName(); 
	}

	public ExpressionPart getRootExpression() {
		return parts.isEmpty() ? null : parts.getFirst();
	}
	
	private static class ToStringVisitor implements ExpressionVisitor {
		private StringBuilder str;
		
		public String buildString(ExpressionPart exp) {
			if (exp == null) {
				return "";
			}
			str = new StringBuilder();
			exp.accept(this);
			return str.toString();
		}
		
		public void visit(ExpressionPart part) {
			throw new IllegalStateException("can't generate text for: " + part.getClass().getName());
		}

		public void visit(ExpressionField part) {
			str.append('.').append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionMethod part) {
			str.append('.').append(part.getName())
				.append('(')
				.append(paramsToString(part.getParams()))
				.append(')');
			moveNext(part);
		}

		public void visit(ExpressionVariable part) {
			str.append(part.getName());
			moveNext(part);			
		}

		public void visit(ExpressionGlobalVariable part) {
			str.append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionCollection part) {
			str.append('.').append(part.getName());
			moveNext(part);
		}

		public void visit(ExpressionCollectionIndex part) {
			str.append('[').append(paramsToString(part.getParams())).append(']');
			moveNext(part);
		}

		public void visit(ExpressionText part) {
			str.append(part.getName());
			moveNext(part);
		}

		private String paramsToString(Map<String, ExpressionFormLine> params) {
			if (params.isEmpty()) {
				return "";
			}
			ToStringVisitor stringVisitor = new ToStringVisitor();
			StringBuilder strParams = new StringBuilder();
			for (ExpressionFormLine param : params.values()) {
				strParams.append(", ").append(stringVisitor.buildString(param.getRootExpression()));
			}
			return strParams.substring(2);
		}
		
		private void moveNext(ExpressionPart exp) {
			if (exp.getNext() != null) {
				exp.getNext().accept(this);
			}
		}
	}
}
