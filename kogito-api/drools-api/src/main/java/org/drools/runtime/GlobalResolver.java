package org.drools.runtime;

public interface GlobalResolver {
    public Object resolveGlobal(String identifier);

    public void setGlobal(String identifier, Object value);
}
