package org.drools.traits.core.factmodel;

import java.io.Serializable;

import org.drools.core.util.Entry;

public interface Triple extends Entry, Serializable {

    Object getInstance();

    Object getProperty();

    Object getValue();

}