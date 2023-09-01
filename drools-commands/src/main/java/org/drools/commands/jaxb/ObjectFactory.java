package org.drools.commands.jaxb;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public JaxbListWrapper createJaxbListWrapper() {
        return new JaxbListWrapper();
    }
}