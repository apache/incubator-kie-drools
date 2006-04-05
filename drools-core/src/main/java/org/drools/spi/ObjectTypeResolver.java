package org.drools.spi;

import java.io.Serializable;

public interface ObjectTypeResolver extends Serializable {
    ObjectType resolve(Object object);    
}
