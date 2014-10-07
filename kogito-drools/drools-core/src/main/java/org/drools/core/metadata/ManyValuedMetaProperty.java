package org.drools.core.metadata;

import java.util.Collection;

public interface ManyValuedMetaProperty<T,R,C extends Collection<R>> extends MetaProperty<T,R,C> {

    public void set( T o, R value, Lit mode );

    public void set( T o, C value, Lit mode );

    public C get( T object );

}
