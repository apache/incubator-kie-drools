package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.GenFnType;

public class FunctionTypeNode extends TypeNode {

    private final List<TypeNode> argTypes;
    private final TypeNode retType;

    public FunctionTypeNode(ParserRuleContext ctx, List<TypeNode> argTypes, TypeNode gen) {
        super( ctx );
        this.argTypes = new ArrayList<>(argTypes);
        this.retType = gen;
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        List<Type> args = argTypes.stream().map(t -> t.evaluate(ctx)).collect(Collectors.toList());
        Type ret = retType.evaluate(ctx);
        return new GenFnType(args, ret);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public List<TypeNode> getArgTypes() {
        return argTypes;
    }

    public TypeNode getRetType() {
        return retType;
    }

}
