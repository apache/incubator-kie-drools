package org.kie.dmn.model.v1x;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public interface DMNModelInstrumentedBase {

    String getNamespaceURI(String prefix);

    DMNModelInstrumentedBase getParentDRDElement();

    Optional<String> getPrefixForNamespaceURI(String namespaceURI);

    void setAdditionalAttributes(Map<QName, String> additionalAttributes);

    Map<QName, String> getAdditionalAttributes();

    DMNModelInstrumentedBase getParent();

    void setParent(DMNModelInstrumentedBase parent);

    List<DMNModelInstrumentedBase> getChildren();

    void addChildren(DMNModelInstrumentedBase child);

    String getIdentifierString();

    String getURIFEEL();

    void setLocation(Location location);

    Location getLocation();

    /**
     * Namespace context map as defined at the level of the given element.
     * Please notice it support also default namespace (no prefix) as "" as defined in {@link XMLConstants#DEFAULT_NS_PREFIX} .
     */
    Map<String, String> getNsContext();
}
