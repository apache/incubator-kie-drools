package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Expression;

public class TChildExpression extends KieDMNModelInstrumentedBase implements ChildExpression {

    protected Expression expression;
    
    protected String id;

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression value) {
        this.expression = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
