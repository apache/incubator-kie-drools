package org.drools.core.util;

import java.io.Serializable;

public interface Iterator<T>
    extends
    Serializable {
    public T next();
}
