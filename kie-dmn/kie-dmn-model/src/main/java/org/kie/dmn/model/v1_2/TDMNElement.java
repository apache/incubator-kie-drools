package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElement;

public class TDMNElement extends KieDMNModelInstrumentedBase implements DMNElement {

    protected String description;
    protected ExtensionElements extensionElements;
    protected String id;
    protected String label;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    @Override
    public void setExtensionElements(ExtensionElements value) {
        this.extensionElements = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String value) {
        this.label = value;
    }

    public static class TExtensionElements extends KieDMNModelInstrumentedBase implements ExtensionElements {

        protected List<Object> any;

        @Override
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }

    }

}
