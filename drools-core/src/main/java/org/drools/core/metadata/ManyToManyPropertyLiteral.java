package org.drools.core.metadata;

import java.net.URI;
import java.util.List;

public abstract class ManyToManyPropertyLiteral<T,R>
        extends ToManyPropertyLiteral<T,R>
        implements ManyToManyValuedMetaProperty<T,R,List<R>,List<T>> {

    public ManyToManyPropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public ManyToManyPropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    @Override
    public void set( T o, List<R> values, Lit mode ) {
        ManyValuedMetaProperty<R,T,List<T>> inverse = getInverse();

        switch ( mode ) {
            case ADD:
                for ( R value : values ) {
                    inverse.set( value, o, Lit.ADD );
                }
                break;
            case SET:
                List<R> current = get( o );
                for ( R cur : current ) {
                    inverse.set( cur, o, Lit.REMOVE );
                }
                for ( R value : values ) {
                    inverse.set( value, o, Lit.ADD );
                }
                break;
            case REMOVE:
                for ( R value : values ) {
                    inverse.set( value, o, Lit.REMOVE );
                }
                break;
        }

        super.set( o, values, mode );
    }
}
