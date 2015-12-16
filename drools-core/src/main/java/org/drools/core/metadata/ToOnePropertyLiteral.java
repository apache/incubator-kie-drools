/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
