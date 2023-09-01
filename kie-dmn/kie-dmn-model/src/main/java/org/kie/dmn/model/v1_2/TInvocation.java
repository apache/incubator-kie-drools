package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.Binding;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.Invocation;

public class TInvocation extends TExpression implements Invocation {

    protected Expression expression;
    protected List<Binding> binding;

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression value) {
        this.expression = value;
    }

    @Override
    public List<Binding> getBinding() {
        if (binding == null) {
            binding = new ArrayList<>();
        }
        return this.binding;
    }

}
