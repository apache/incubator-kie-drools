package org.drools.core.metadata;

//TODO FIXME make this extend MetaProperty
public interface InvertibleMetaProperty<T,R,D> {

    //TODO FIXME make this return an invertible MetaProperty
    public MetaProperty<R,T,D> getInverse();

}
