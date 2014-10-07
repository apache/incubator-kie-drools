package org.drools.core.metadata;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public abstract class ToManyPropertyLiteral<T,R,C extends List<R>>
        extends PropertyLiteral<T,R,C>
        implements ManyValuedMetaProperty<T,R,C> {

    public ToManyPropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public ToManyPropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    public abstract void set( T o, List<R> values );

    @Override
    public void set( T o, C values, Lit mode ) {
        switch ( mode ) {
            case SET:
                set( o, values );
                break;
            case ADD:
                get( o ).addAll( values );
                break;
            case REMOVE:
                get( o ).removeAll( values );
                break;
        }
    }

    @Override
    public void set( T o, R value, Lit mode ) {
        switch ( mode ) {
            case SET:
                set( o, Collections.singletonList( value ) );
                break;
            case ADD:
                get( o ).add( value );
                break;
            case REMOVE:
                get( o ).remove( value );
                break;
        }
    }

    @Override
    public boolean isManyValued() {
        return true;
    }

    @Override
    public OneValuedMetaProperty<T,C> asFunctionalProperty() {
        return (OneValuedMetaProperty<T,C>) this;
    }

    @Override
    public ManyValuedMetaProperty<T,R,C> asManyValuedProperty() {
        return this;
    }
}
