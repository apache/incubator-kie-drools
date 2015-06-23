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
