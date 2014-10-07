package org.drools.core.metadata;

import java.net.URI;
import java.util.Collection;

public abstract class ToOnePropertyLiteral<T,R>
        extends PropertyLiteral<T,R,R>
        implements OneValuedMetaProperty<T,R> {

    public ToOnePropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public ToOnePropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    public abstract void set( T object, R value );

    public void set( T object, R value, Lit lit ) {
        switch ( lit ) {
            case SET:
            case ADD:
                    set( object, value );
                break;
            case REMOVE:
                set( object, null);
        }
    }

    @Override
    public boolean isManyValued() {
        return false;
    }

    @Override
    public OneValuedMetaProperty<T,R> asFunctionalProperty() {
        return this;
    }

    @Override
    public ManyValuedMetaProperty<T,R,Collection<R>> asManyValuedProperty() {
        throw new ClassCastException( "Single valued property " + getName() + " can't be used as a Many-valued property" );
    }
}
