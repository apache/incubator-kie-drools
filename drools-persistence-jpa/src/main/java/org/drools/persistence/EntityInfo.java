package org.drools.persistence;

public interface EntityInfo {
    long getId();
    int getVersion();
    void update();
}