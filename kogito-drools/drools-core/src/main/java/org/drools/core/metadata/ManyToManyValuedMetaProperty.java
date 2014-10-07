package org.drools.core.metadata;


import java.util.Collection;

public interface ManyToManyValuedMetaProperty<T,R,C extends Collection<R>,D extends Collection<T>>
        extends ManyValuedMetaProperty<T,R,C>, InverseManyValuedMetaProperty<T,R,D> {

    @Override
    public ManyValuedMetaProperty<R,T,D> getInverse();
}
