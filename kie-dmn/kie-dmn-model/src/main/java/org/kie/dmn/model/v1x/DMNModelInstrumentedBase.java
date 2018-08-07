package org.kie.dmn.model.v1x;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

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

}
