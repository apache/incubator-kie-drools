package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;

public class TContextEntry extends KieDMNModelInstrumentedBase implements ContextEntry, NotADMNElementInV11 {

    private InformationItem variable;
    private Expression expression;

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem value) {
        this.variable = value;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression value) {
        this.expression = value;
    }

}
