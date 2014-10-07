package org.drools.core.metadata;

public interface InvertibleMetaProperty<T,R,D> {

    public MetaProperty<R,T,D> getInverse();

}
