package org.drools.core.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ToManyPropertyLiteral<T,R>
        extends PropertyLiteral<T,R,List<R>>
        implements ManyValuedMetaProperty<T,R,List<R>> {

    public ToManyPropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public ToManyPropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    public abstract void set( T o, List<R> values );

    @Override
    public void set( T o, List<R> values, Lit mode ) {
        switch ( mode ) {
            case SET:
                set( o, new ArrayList( values ) );
                break;
            case ADD:
                List<R> list = get( o );
                if ( list == null ) {
                    list = new ArrayList();
                    set( o, list );
                }
                list.addAll( values );
                break;
            case REMOVE:
                List<R> curr = get( o );
                if ( curr != null ) {
                    curr.removeAll( values );
                }
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
                List<R> list = get( o );
                if ( list == null ) {
                    list = new ArrayList();
                    set( o, list );
                }
                list.add( value );
                break;
            case REMOVE:
                List<R> curr = get( o );
                if ( curr != null ) {
                    curr.remove( value );
                }
                break;
        }
    }

    @Override
    public boolean isManyValued() {
        return true;
    }

    @Override
    public OneValuedMetaProperty<T,List<R>> asFunctionalProperty() {
        return (OneValuedMetaProperty<T,List<R>>) this;
    }

    @Override
    public ManyValuedMetaProperty<T,R,List<R>> asManyValuedProperty() {
        return this;
    }
}
