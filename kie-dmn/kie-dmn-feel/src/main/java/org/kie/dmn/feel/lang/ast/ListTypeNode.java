package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.GenListType;

public class ListTypeNode extends TypeNode {

    private final TypeNode genTypeNode;

    public ListTypeNode(ParserRuleContext ctx, TypeNode gen) {
        super( ctx );
        this.genTypeNode = gen;
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        Type gen = genTypeNode.evaluate(ctx);
        return new GenListType(gen);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public TypeNode getGenTypeNode() {
        return genTypeNode;
    }
}
