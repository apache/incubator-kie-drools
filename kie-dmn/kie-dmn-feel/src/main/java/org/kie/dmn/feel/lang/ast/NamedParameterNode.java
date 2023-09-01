package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;


public class NamedParameterNode
        extends BaseNode {

    private NameDefNode name;
    private BaseNode expression;

    public NamedParameterNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression) {
        super( ctx );
        this.name = name;
        this.expression = expression;
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

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        String n = name.evaluate( ctx );
        Object val = expression.evaluate( ctx );
        return new NamedParameter( n, val );
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { name, expression };
    }


    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
