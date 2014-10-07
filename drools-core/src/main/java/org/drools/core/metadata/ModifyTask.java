package org.drools.core.metadata;

public interface ModifyTask<T> {

    public MetaProperty<T,?,?> getProperty();

    public Object getValue();

    public ModifyTask<T> getNext();

}
