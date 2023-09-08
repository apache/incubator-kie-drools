/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.metadata;

import java.net.URI;
import java.util.List;

public abstract class OneToManyPropertyLiteral<T,R>
        extends ToManyPropertyLiteral<T,R>
        implements OneToManyValuedMetaProperty<T,R,List<R>> {

    public OneToManyPropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public OneToManyPropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    @Override
    public void set( T o, List<R> values, Lit mode ) {
        OneValuedMetaProperty<R,T> inv = getInverse();

        switch ( mode ) {
            case SET:
            case ADD:
                for ( R value : values ) {
                    T t = inv.get( value );
                    if ( t != null ) {
                        set( t, value, Lit.REMOVE );
                    }
                    inv.set( value, o );
                }
                break;
            case REMOVE:
                for ( R value : values ) {
                    inv.set( value, null );
                }
        }

        super.set( o, values, mode );
    }

}
