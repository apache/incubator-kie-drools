package org.kie.internal.builder;

public enum ChangeType {
    REMOVED, UPDATED, ADDED;
    
    public String toString() {
        return super.toString().toLowerCase();
    }
}
