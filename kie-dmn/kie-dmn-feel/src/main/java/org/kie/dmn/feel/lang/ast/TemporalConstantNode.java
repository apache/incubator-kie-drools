package org.kie.dmn.feel.lang.ast;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;

public class TemporalConstantNode extends BaseNode {

    public final Object value;
    public final FEELFunction fn;
    public final List<Object> params;

    public TemporalConstantNode(FunctionInvocationNode orig, Object value, FEELFunction fn, List<Object> params) {
        copyLocationAttributesFrom(orig);
        this.value = value;
        this.fn = fn;
        this.params = Collections.unmodifiableList(params);
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return value;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.UNKNOWN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
