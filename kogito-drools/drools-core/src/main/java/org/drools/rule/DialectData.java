package org.drools.rule;

public interface DialectData extends Cloneable {
    public void removeRule(Package pkg, Rule rule);

    public void removeFunction(Package pkg, Function function);

    public void merge(DialectData newData);

    public boolean isDirty();

    public void setDirty(boolean dirty);

    public void reload();

    public DialectData clone();

    public void setDialectDatas(DialectDatas datas);
}
