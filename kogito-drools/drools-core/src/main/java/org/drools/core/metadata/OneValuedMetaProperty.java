package org.drools.core.metadata;


public interface OneValuedMetaProperty<T,R> extends MetaProperty<T,R,R> {

    public void set( T o, R value );

    public R get( T object );

    public void set( T target, R value, Lit set );
}
