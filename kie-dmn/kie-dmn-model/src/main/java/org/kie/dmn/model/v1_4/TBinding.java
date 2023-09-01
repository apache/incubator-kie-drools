package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.Binding;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;

public class TBinding extends KieDMNModelInstrumentedBase implements Binding {

    protected InformationItem parameter;
    protected Expression expression;

    @Override
    public InformationItem getParameter() {
        return parameter;
    }

    @Override
    public void setParameter(InformationItem value) {
        this.parameter = value;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression value) {
        this.expression = value;
    }

}
