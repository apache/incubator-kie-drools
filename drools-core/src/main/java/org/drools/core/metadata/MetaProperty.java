package org.drools.core.metadata;

import java.net.URI;

public interface MetaProperty<T,R> extends Comparable<MetaProperty<T,R>>, Identifiable {

    public int getIndex();

    public String getName();

    public URI getKey();

    public R get( T object );

    public void set( T o, R value );
}
