package org.drools.util;

import java.io.Serializable;

public interface Iterator extends Serializable {
    public Entry next();
}