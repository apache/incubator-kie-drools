package org.drools.model;

public interface DomainClassMetadata {
    Class<?> getDomainClass();
    int getPropertiesSize();
    int getPropertyIndex(String name);
}
