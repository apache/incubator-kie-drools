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
import java.util.ArrayList;
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
                if ( current != null ) {
                    if ( ! current.isEmpty() ) {
                        for ( R cur : current ) {
                            inverse.set( cur, o, Lit.REMOVE );
                        }
                    }
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
