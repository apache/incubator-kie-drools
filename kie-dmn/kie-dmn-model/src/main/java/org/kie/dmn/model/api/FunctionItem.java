package org.kie.dmn.model.api;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @since DMN v1.3
 */
public interface FunctionItem extends DMNElement {

    List<InformationItem> getParameters();

    /**
     * for consistency in the modeling, use QName
     */
    QName getOutputTypeRef();

    void setOutputTypeRef(QName value);

}
