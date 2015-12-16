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
