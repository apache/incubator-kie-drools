package org.drools.model;

public interface NamedModelItem {
    String getPackage();
    String getName();

    default String getFullName() {
        return getPackage() + "." + getName();
    }
}
