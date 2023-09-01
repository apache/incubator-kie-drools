package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.EvalHelper;

public class StringNode extends BaseNode {
    private final String value;

    public StringNode(ParserRuleContext ctx) {
        super( ctx );
        this.value = EvalHelper.unescapeString(getText());
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return getValue();
    }

    public String getValue() {
        return value;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.STRING;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
