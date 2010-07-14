package org.drools.common;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public DisconnectedFactHandle createDisconnectedFactHandle() {
        return new DisconnectedFactHandle();
    }
    
    public DefaultFactHandle createDefaultFactHandle() {
        return new DefaultFactHandle();
    }
}
