package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElement;

public interface NotADMNElementInV11 extends DMNElement {

    @Override
    default String getDescription() {
        return null;
    }

    @Override
    default void setDescription(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default ExtensionElements getExtensionElements() {
        return null;
    }

    @Override
    default void setExtensionElements(ExtensionElements value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getId() {
        return null;
    }

    @Override
    default void setId(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getLabel() {
        return null;
    }

    @Override
    default void setLabel(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }
}
