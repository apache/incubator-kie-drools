package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class BooleanNode
        extends BaseNode {

    Boolean value;

    public BooleanNode(ParserRuleContext ctx) {
        super( ctx );
        value = Boolean.valueOf( ctx.getText() );
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return value;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.BOOLEAN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
