package org.kie.dmn.model.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    <T extends DMNModelInstrumentedBase> List<? extends T> findAllChildren(Class<T> clazz);

    String getIdentifierString();

    String getURIFEEL();

    void setLocation(Location location);

    Location getLocation();

    /**
     * Namespace context map as defined at the level of the given element.
     * Please notice it support also default namespace (no prefix) as "" as defined in {@link XMLConstants#DEFAULT_NS_PREFIX} .
     */
    Map<String, String> getNsContext();

    default Set<String> recurseNsKeys() {
        Set<String> res = new HashSet<>();
        if (getParent() != null) {
            res.addAll(getParent().recurseNsKeys());
        }
        res.addAll(getNsContext().keySet());
        return res;
    }

}
