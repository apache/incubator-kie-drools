package org.drools.spi;

import java.io.Serializable;

public interface GlobalResolver extends Serializable {
    public Object resolveGlobal(String identifier);
    
    public void setGlobal(String identifier, Object value);
}
