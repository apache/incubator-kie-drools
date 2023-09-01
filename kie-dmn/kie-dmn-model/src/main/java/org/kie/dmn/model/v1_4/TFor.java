package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.For;

public class TFor extends TIterator implements For {
    
    protected ChildExpression _return;

    public ChildExpression getReturn() {
        return _return;
    }

    public void setReturn(ChildExpression value) {
        this._return = value;
    }

}
