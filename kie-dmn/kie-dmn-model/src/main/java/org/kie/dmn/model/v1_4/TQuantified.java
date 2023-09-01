package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Quantified;

public class TQuantified extends TIterator implements Quantified {
    
    protected ChildExpression satisfies;

    public ChildExpression getSatisfies() {
        return satisfies;
    }

    public void setSatisfies(ChildExpression value) {
        this.satisfies = value;
    }

}
