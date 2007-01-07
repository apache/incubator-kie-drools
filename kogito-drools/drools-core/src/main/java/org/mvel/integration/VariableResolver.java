package org.mvel.integration;

public interface VariableResolver {
    public String getName();
    public Class getKnownType();
    public int getFlags();
    public Object getValue();
}
