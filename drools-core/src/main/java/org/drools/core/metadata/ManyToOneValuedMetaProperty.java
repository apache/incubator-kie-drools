package org.drools.core.metadata;


import java.util.Collection;

public interface ManyToOneValuedMetaProperty<T,R,C extends Collection<T>>
        extends OneValuedMetaProperty<T,R>, InverseManyValuedMetaProperty<T,R,C> {

    @Override
    public ManyValuedMetaProperty<R,T,C> getInverse();
}
