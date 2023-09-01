package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Expression;

public abstract class TExpression extends TDMNElement implements Expression {

    private QName typeRef;

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef( final QName value ) {
        this.typeRef = value;
    }

}
