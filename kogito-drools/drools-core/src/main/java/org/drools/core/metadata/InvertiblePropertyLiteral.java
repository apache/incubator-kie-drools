package org.drools.core.metadata;

import java.net.URI;
import java.util.Collection;

public abstract class InvertiblePropertyLiteral extends PropertyLiteral implements InvertibleMetaProperty {

    public InvertiblePropertyLiteral( int index, Class klass, String name ) {
        super( index, klass, name );
    }

    public InvertiblePropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    /*
    public void set( T o, R value, Lit forw, Lit back ) {
        this.setOneWay( o, value, forw );
        getInverse().setOneWay( value, o, back );
    }
    */
}
