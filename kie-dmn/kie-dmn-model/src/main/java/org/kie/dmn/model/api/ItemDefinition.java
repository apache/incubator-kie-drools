package org.kie.dmn.model.api;

import java.util.List;

import javax.xml.namespace.QName;

public interface ItemDefinition extends NamedElement {

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    QName getTypeRef();

    /**
     * Internal Model: this is using QName as per DMN v1.1 in order to maintain internal compatibility with the engine
     */
    void setTypeRef(QName value);

    UnaryTests getAllowedValues();

    void setAllowedValues(UnaryTests value);

    List<ItemDefinition> getItemComponent();

    String getTypeLanguage();

    void setTypeLanguage(String value);

    boolean isIsCollection();

    void setIsCollection(Boolean value);

    String toString();

    /**
     * @since DMN v1.3
     */
    FunctionItem getFunctionItem();

    /**
     * @since DMN v1.3
     */
    void setFunctionItem(FunctionItem value);

}
