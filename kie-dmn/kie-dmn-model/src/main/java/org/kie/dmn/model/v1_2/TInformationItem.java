package org.kie.dmn.model.v1_2;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.InformationItem;

public class TInformationItem extends TNamedElement implements InformationItem {

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
