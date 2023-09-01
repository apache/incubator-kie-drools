package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;

public class IterationContextNode
        extends BaseNode {

    private NameDefNode name;
    private BaseNode    expression;
    private BaseNode    rangeEndExpr = null;

    public IterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression) {
        super( ctx );
        this.name = name;
        this.expression = expression;
    }

    public IterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression, BaseNode rangeEndExpr) {
        super(ctx);
        this.name = name;
        this.expression = expression;
        this.rangeEndExpr = rangeEndExpr;
    }

    public NameDefNode getName() {
        return name;
    }

    public void setName(NameDefNode name) {
        this.name = name;
    }

    public BaseNode getExpression() {
        return expression;
    }

    public BaseNode getRangeEndExpr() {
        return rangeEndExpr;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public String evaluateName(EvaluationContext ctx) {
        return this.name.evaluate(ctx);
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return expression != null ? expression.evaluate( ctx ) : null;
    }

    public Object evaluateRangeEnd(EvaluationContext ctx) {
        return rangeEndExpr != null ? rangeEndExpr.evaluate(ctx) : null;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        if( rangeEndExpr != null ) {
            return new ASTNode[] { name, expression, rangeEndExpr };
        }
        return new ASTNode[] { name, expression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
