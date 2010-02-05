package org.drools.guvnor.client.modeldriven.brl;


public class ExpressionVariable extends ExpressionPart {
	private FactPattern fact;
	
	@SuppressWarnings("unused")
	private ExpressionVariable() {}

	public ExpressionVariable(FactPattern fact) {
		super(fact.boundName, fact.factType, fact.factType);
		if (!fact.isBound()) {
			throw new RuntimeException("the fact is not bounded: " + fact);
		}
		this.fact = fact;
	}

	public FactPattern getFact() {
		return fact;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
