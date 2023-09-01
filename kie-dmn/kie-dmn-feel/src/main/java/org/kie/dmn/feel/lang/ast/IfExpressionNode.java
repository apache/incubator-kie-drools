package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class IfExpressionNode
        extends BaseNode {

    private BaseNode condition;
    private BaseNode thenExpression;
    private BaseNode elseExpression;

    public IfExpressionNode(ParserRuleContext ctx, BaseNode condition, BaseNode thenExpression, BaseNode elseExpression) {
        super( ctx );
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    public BaseNode getCondition() {
        return condition;
    }

    public void setCondition(BaseNode condition) {
        this.condition = condition;
    }

    public BaseNode getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(BaseNode thenExpression) {
        this.thenExpression = thenExpression;
    }

    public BaseNode getElseExpression() {
        return elseExpression;
    }

    public void setElseExpression(BaseNode elseExpression) {
        this.elseExpression = elseExpression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        // spec says: if FEEL(e1) is true then FEEL(e2) else FEEL(e3)
        Object cond = this.condition.evaluate( ctx );
        if (cond == Boolean.TRUE) {
            return this.thenExpression.evaluate( ctx );
        } else {
            return this.elseExpression.evaluate( ctx );
        }
    }

    @Override
    public Type getResultType() {
        if ( thenExpression.getResultType().equals(elseExpression.getResultType()) ) {
            return thenExpression.getResultType();
        } else {
            return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { condition, thenExpression, elseExpression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
