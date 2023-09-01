package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public abstract class TypeNode
        extends BaseNode {

    public TypeNode() {
        super();
    }

    public TypeNode(ParserRuleContext ctx) {
        super( ctx );
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        return BuiltInType.determineTypeFromName( getText() );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
