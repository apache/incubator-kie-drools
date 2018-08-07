package org.kie.dmn.model.v1x;

public interface DMNElement extends DMNModelInstrumentedBase {

    String getDescription();

    void setDescription(String value);

    ExtensionElements getExtensionElements();

    void setExtensionElements(ExtensionElements value);

    String getId();

    void setId(String value);

    String getLabel();

    void setLabel(String value);

}
