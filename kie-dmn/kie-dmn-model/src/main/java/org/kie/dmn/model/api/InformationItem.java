package org.kie.dmn.model.api;

import javax.xml.namespace.QName;

public interface InformationItem extends NamedElement {

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    QName getTypeRef();

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    void setTypeRef(QName value);

}
