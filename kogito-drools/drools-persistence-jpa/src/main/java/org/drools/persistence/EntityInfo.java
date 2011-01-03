package org.drools.persistence;

public interface EntityInfo {
    Long getId();
    int getVersion();
    void update();
}