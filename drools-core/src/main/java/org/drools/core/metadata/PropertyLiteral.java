/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.metadata;

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

    public abstract boolean isDatatype();

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
