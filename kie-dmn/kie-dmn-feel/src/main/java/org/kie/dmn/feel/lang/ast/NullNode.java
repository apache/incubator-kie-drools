package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;

public class NullNode
        extends BaseNode {

    public NullNode(ParserRuleContext ctx) {
        super( ctx );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return null;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
