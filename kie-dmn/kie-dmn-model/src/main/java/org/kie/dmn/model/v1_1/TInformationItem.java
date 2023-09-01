package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.InformationItem;

public class TInformationItem extends TNamedElement implements InformationItem {

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
