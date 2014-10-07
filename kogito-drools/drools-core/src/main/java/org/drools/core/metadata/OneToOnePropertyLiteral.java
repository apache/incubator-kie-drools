package org.drools.core.metadata;

import java.net.URI;

public abstract class OneToOnePropertyLiteral<T,R>
        extends ToOnePropertyLiteral<T,R>
        implements OneToOneValuedMetaProperty<T,R> {

    public OneToOnePropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public OneToOnePropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    @Override
    public void set( T object, R value, Lit lit ) {
        if ( value == null ) {
            getInverse().set( get( object ), null );
        } else {
            getInverse().set( value, object );
        }
        super.set( object, value, lit );
    }

}
