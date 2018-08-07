package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.v1x.DMNElement;
import org.kie.dmn.model.v1x.ExtensionElements;

public interface NotADMNElementInV11 extends DMNElement {

    @Override
    default String getDescription() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default void setDescription(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default ExtensionElements getExtensionElements() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default void setExtensionElements(ExtensionElements value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getId() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default void setId(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default String getLabel() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

    @Override
    default void setLabel(String value) {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }
}
