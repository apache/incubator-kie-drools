package org.drools.core.metadata;


import java.util.Collection;

public interface InverseManyValuedMetaProperty<T,R,C extends Collection<T>> extends InvertibleMetaProperty<T,R,C> {

    public ManyValuedMetaProperty<R,T,C> getInverse();

}
