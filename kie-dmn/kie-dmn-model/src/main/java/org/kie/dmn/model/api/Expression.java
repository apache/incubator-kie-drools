package org.kie.dmn.model.api;

import javax.xml.namespace.QName;

public interface Expression extends DMNElement {

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     * DMN13-140/DMN13-168: this is optional attribute.
     */
    QName getTypeRef();

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     * DMN13-140/DMN13-168: this is optional attribute.
     */
    void setTypeRef(QName value);

}
