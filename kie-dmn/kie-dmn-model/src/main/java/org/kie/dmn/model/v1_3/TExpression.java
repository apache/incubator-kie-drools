package org.kie.dmn.model.v1_3;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Expression;

public class TExpression extends TDMNElement implements Expression {

    /**
     * align with internal model
     */
    protected QName typeRef;

    @Override
    public QName getTypeRef() {
        return this.typeRef;
    }

    @Override
    public void setTypeRef(QName value) {
        this.typeRef = value;
    }

}
