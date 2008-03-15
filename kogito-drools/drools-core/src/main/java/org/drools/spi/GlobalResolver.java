package org.drools.spi;

import java.io.Externalizable;

public interface GlobalResolver extends Externalizable {
    public Object resolveGlobal(String identifier);

    public void setGlobal(String identifier, Object value);
}
