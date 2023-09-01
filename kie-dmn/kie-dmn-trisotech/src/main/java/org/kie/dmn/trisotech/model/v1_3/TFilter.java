package org.kie.dmn.trisotech.model.v1_3;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_3.TExpression;
import org.kie.dmn.trisotech.model.api.Filter;

public class TFilter extends TExpression implements Filter {

    private Expression in;
    private Expression match;

    @Override
    public Expression getIn() {
        return in;
    }

    @Override
    public Expression getMatch() {
        return match;
    }

    @Override
    public void setIn(Expression expr) {
        this.in = expr;

    }

    @Override
    public void setMatch(Expression expr) {
        this.match = expr;

    }

}
