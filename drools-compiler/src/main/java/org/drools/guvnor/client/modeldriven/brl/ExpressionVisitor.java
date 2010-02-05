package org.drools.guvnor.client.modeldriven.brl;

public interface ExpressionVisitor {
	void visit(ExpressionPart part);
	void visit(ExpressionField part);
	void visit(ExpressionMethod part);
	void visit(ExpressionVariable part);
	void visit(ExpressionGlobalVariable part);
	void visit(ExpressionCollection part);
	void visit(ExpressionCollectionIndex part);
	void visit(ExpressionText part);
}
