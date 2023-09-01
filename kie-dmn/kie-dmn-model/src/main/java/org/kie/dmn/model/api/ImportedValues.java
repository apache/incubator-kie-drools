package org.kie.dmn.model.api;

public interface ImportedValues extends Import {

    String getImportedElement();

    void setImportedElement(String value);

    String getExpressionLanguage();

    void setExpressionLanguage(String value);

}
