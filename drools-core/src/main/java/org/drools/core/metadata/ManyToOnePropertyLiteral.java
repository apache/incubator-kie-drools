package org.drools.core.metadata;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public abstract class ManyToOnePropertyLiteral<T,R>
        extends ToOnePropertyLiteral<T,R>
        implements ManyToOneValuedMetaProperty<T,R,List<T>> {


    public ManyToOnePropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public ManyToOnePropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }


    @Override
    public void set( T object, R value, Lit lit ) {
        ManyValuedMetaProperty<R,T,List<T>> inverse = getInverse();

        if ( value != null ) {
            R prev = this.get( object );
            if ( prev != null ) {
                inverse.set( prev, object, Lit.REMOVE );
            }

            inverse.set( value, object, lit != Lit.REMOVE ? Lit.ADD : Lit.REMOVE );
        }

        super.set( object, value, lit );
    }
}
