package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;

public class TContextEntry extends TDMNElement implements ContextEntry {

    protected InformationItem variable;
    protected Expression expression;

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(InformationItem value) {
        this.variable = value;
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
