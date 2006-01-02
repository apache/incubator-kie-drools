package org.drools.spi;

import java.io.Serializable;

public interface Extractor
    extends
    Serializable {
    Object getValue(Object object);
    
    Class getValueType();
}
