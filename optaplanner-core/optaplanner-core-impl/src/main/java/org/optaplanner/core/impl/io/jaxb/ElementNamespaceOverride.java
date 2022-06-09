package org.optaplanner.core.impl.io.jaxb;

public class ElementNamespaceOverride {

    public static ElementNamespaceOverride of(String elementLocalName, String namespaceOverride) {
        return new ElementNamespaceOverride(elementLocalName, namespaceOverride);
    }

    private final String elementLocalName;
    private final String namespaceOverride;

    private ElementNamespaceOverride(String elementLocalName, String namespaceOverride) {
        this.elementLocalName = elementLocalName;
        this.namespaceOverride = namespaceOverride;
    }

    public String getElementLocalName() {
        return elementLocalName;
    }

    public String getNamespaceOverride() {
        return namespaceOverride;
    }

    @Override
    public String toString() {
        return "ElementNamespaceOverride{" +
                "elementLocalName='" + elementLocalName + '\'' +
                ", namespaceOverride='" + namespaceOverride + '\'' +
                '}';
    }
}
