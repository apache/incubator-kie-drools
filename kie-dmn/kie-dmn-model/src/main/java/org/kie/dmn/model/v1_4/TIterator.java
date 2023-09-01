package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.Iterator;
import org.kie.dmn.model.api.TypedChildExpression;

public class TIterator extends TExpression implements Iterator {
    
	protected TypedChildExpression in;
	
    protected String iteratorVariable;

    public TypedChildExpression getIn() {
        return in;
    }

    public void setIn(TypedChildExpression value) {
        this.in = value;
    }

    public String getIteratorVariable() {
        return iteratorVariable;
    }

    public void setIteratorVariable(String value) {
        this.iteratorVariable = value;
    }

}
