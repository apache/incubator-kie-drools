package org.drools.core.common;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    
    public DefaultFactHandle createDefaultFactHandle() {
        return new DefaultFactHandle();
    }
}
