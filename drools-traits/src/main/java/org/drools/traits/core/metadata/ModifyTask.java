package org.drools.traits.core.metadata;

public interface ModifyTask<T> {

    public MetaProperty<T,?,?> getProperty();

    public Object getValue();

    public ModifyTask<T> getNext();

    public Lit getMode();

}
