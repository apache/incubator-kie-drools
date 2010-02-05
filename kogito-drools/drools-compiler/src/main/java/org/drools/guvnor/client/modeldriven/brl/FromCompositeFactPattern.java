package org.drools.guvnor.client.modeldriven.brl;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class FromCompositeFactPattern implements IPattern {

    public FactPattern factPattern;
    private ExpressionFormLine expression = new ExpressionFormLine();

    public FromCompositeFactPattern() {
    }

    public ExpressionFormLine getExpression() {
        return expression;
    }

    public void setExpression(ExpressionFormLine expression) {
        this.expression = expression;
    }

    public FactPattern getFactPattern() {
        return factPattern;
    }

    public void setFactPattern(FactPattern pattern) {
        this.factPattern = pattern;
    }


}
