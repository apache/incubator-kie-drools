package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;

public class NumberNode
        extends BaseNode {

    BigDecimal value;

    public NumberNode(ParserRuleContext ctx) {
        super( ctx );
        value = EvalHelper.getBigDecimalOrNull( ctx.getText() );
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return value;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.NUMBER;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
