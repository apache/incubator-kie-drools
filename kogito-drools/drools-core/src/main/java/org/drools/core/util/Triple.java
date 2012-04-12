package org.drools.core.util;

import java.io.Serializable;

public interface Triple extends Entry, Serializable {

    public abstract Object getInstance();

    public abstract Object getProperty();

    public abstract Object getValue();

}