package org.kie.util;

public enum ChangeType {
    REMOVED, UPDATED, ADDED;
    
    public String toString() {
        return super.toString().toLowerCase();
    }
}