package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;


public class ContextEntryNode
        extends BaseNode {

    private BaseNode name;
    private BaseNode value;

    public ContextEntryNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public ContextEntryNode(ParserRuleContext ctx, BaseNode name, BaseNode value) {
        super( ctx );
        this.name = name;
        this.value = value;
    }

    public BaseNode getName() {
        return name;
    }

    public void setName(BaseNode name) {
        this.name = name;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    public String evaluateName( EvaluationContext ctx ) {
        return (String) name.evaluate( ctx );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return value.evaluate( ctx );
    }

    @Override
    public Type getResultType() {
        return value.getResultType();
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { name, value };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
