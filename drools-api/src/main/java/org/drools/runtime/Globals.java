package org.drools.runtime;

public interface Globals {
    Object get(String identifier);

    void set(String identifier,
             Object value);
    
    void setDelegate(Globals delegate);
}
