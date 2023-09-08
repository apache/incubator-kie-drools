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

public abstract class OneToOnePropertyLiteral<T,R>
        extends ToOnePropertyLiteral<T,R>
        implements OneToOneValuedMetaProperty<T,R> {

    public OneToOnePropertyLiteral( int index, Class<T> klass, String name ) {
        super( index, klass, name );
    }

    public OneToOnePropertyLiteral( int index, String name, URI key ) {
        super( index, name, key );
    }

    @Override
    public void set( T object, R value, Lit lit ) {
        if ( value == null ) {
            getInverse().set( get( object ), null );
        } else {
            getInverse().set( value, object );
        }
        super.set( object, value, lit );
    }

}
