package org.kie.dmn.model.api;

import java.util.List;

public interface DMNElement extends DMNModelInstrumentedBase {

    public static interface ExtensionElements {

        List<Object> getAny();

    }

    String getDescription();

    void setDescription(String value);

    ExtensionElements getExtensionElements();

    void setExtensionElements(ExtensionElements value);

    String getId();

    void setId(String value);

    String getLabel();

    void setLabel(String value);

}
