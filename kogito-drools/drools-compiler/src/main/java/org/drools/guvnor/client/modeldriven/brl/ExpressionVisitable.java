package org.drools.guvnor.client.modeldriven.brl;

public interface ExpressionVisitable {
	void accept(ExpressionVisitor visitor);
}
