package org.drools.core.metadata;


import java.util.Collection;

public interface OneToManyValuedMetaProperty<T,R,C extends Collection<R>>
        extends ManyValuedMetaProperty<T,R,C>, InverseOneValuedMetaProperty<T,R> {

    @Override
    public OneValuedMetaProperty<R,T> getInverse();
}
