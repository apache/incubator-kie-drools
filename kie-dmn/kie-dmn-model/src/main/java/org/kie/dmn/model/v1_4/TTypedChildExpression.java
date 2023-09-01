package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.TypedChildExpression;

public class TTypedChildExpression extends TChildExpression implements TypedChildExpression {
    
    protected String typeRef;

    @Override
    public String getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(String value) {
        this.typeRef = value;
    }

}
