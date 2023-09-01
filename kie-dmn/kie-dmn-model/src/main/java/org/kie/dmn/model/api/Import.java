package org.kie.dmn.model.api;

public interface Import extends NamedElement {

    String getNamespace();

    void setNamespace(String value);

    String getLocationURI();

    void setLocationURI(String value);

    String getImportType();

    void setImportType(String value);

}
