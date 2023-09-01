package org.kie.dmn.backend.marshalling.v1x;

import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;

public final class DMNMarshallerFactory {

    public static DMNMarshaller newDefaultMarshaller() {
        return new XStreamMarshaller();
    }

    public static DMNMarshaller newMarshallerWithExtensions(List<DMNExtensionRegister> extensionElementRegisters) {
        return new XStreamMarshaller(extensionElementRegisters);
    }

    private DMNMarshallerFactory() {
        // Constructing instances is not allowed for this class
    }
}
