package org.drools.core.metadata;

import org.kie.api.definition.type.Position;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class PropertyLiteral<T,R,C> implements MetaProperty<T,R,C>, Serializable {

    private final int index;
    private final String name;

    private final URI key;

    public PropertyLiteral( int index, Class<T> klass, String name ) {
        this( index, name, asURI( name, klass ) );
    }

    private static <T> URI asURI( String name, Class klass ) {
        if ( klass == null ) {
            klass = PropertyLiteral.class;
        }
        return URI.create( "http://" + klass.getPackage().getName() + "/" + name );
    }

    public PropertyLiteral( int index, String name, URI key ) {
        this.index = index;
        this.name = name;
        this.key = key;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        PropertyLiteral that = (PropertyLiteral) o;

        if ( !key.equals( that.key ) ) return false;

        return true;
    }

    @Override
    public int compareTo( MetaProperty<T,R,C> o ) {
        return this.getName().compareTo( o.getName() );
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public URI getKey() {
        return key;
    }

    @Override
    public URI getUri() {
        return key;
    }

    @Override
    public Object getId() {
        return key;
    }
}
