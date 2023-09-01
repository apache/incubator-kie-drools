package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Filter;

public class TFilter extends TExpression implements Filter {

    private ChildExpression in;
    private ChildExpression match;

    @Override
    public ChildExpression getIn() {
        return in;
    }

    @Override
    public ChildExpression getMatch() {
        return match;
    }

    @Override
    public void setIn(ChildExpression value) {
        this.in = value;
    }

    @Override
    public void setMatch(ChildExpression value) {
        this.match = value;
    }

}
