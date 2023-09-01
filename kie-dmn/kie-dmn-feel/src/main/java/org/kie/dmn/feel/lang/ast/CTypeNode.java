package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;

public class CTypeNode extends TypeNode {

    private final Type type;

    public CTypeNode(Type type) {
        super();
        this.type = type;
    }

    public CTypeNode(ParserRuleContext ctx, Type type) {
        super( ctx );
        this.type = type;
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        return type;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public Type getType() {
        return type;
    }
}
