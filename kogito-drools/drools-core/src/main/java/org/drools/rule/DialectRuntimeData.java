package org.drools.rule;

public interface DialectRuntimeData extends Cloneable {
    public void removeRule(Package pkg, Rule rule);

    public void removeFunction(Package pkg, Function function);

    public void merge(DialectRuntimeData newData);

    public boolean isDirty();

    public void setDirty(boolean dirty);

    public void reload();

    public DialectRuntimeData clone();
}
