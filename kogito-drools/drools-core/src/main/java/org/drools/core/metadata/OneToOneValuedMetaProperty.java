package org.drools.core.metadata;


public interface OneToOneValuedMetaProperty<T,R>
        extends OneValuedMetaProperty<T,R>, InverseOneValuedMetaProperty<T,R> {

    @Override
    public OneValuedMetaProperty<R, T> getInverse();
}
