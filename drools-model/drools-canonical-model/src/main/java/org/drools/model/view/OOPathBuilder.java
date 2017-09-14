/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.view;

import java.util.LinkedList;

import org.drools.model.Source;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.OOPathViewItem.OOPathChunk;

public class OOPathBuilder<T> {

    private final Source<T> source;

    private final LinkedList<OOPathChunk<?>> chunks = new LinkedList<>();

    public OOPathBuilder( Source<T> source ) {
        this.source = source;
    }

    public OOPathChunkBuilder<T, T> firstChunk() {
        OOPathChunk<T> chunk = new OOPathChunk<T>();
        chunks.add( chunk );
        return new OOPathChunkBuilder<T, T>( this, chunk );
    }

    public static class OOPathChunkBuilder<S, T> implements ViewItemBuilder<T> {

        private final OOPathBuilder<S> builder;
        private final OOPathChunk<T> chunk;

        public OOPathChunkBuilder( OOPathBuilder<S> builder, OOPathChunk<T> chunk ) {
            this.builder = builder;
            this.chunk = chunk;
        }

        public <V> OOPathChunkBuilder<S, V> map( Function1<T, Iterable<V>> map ) {
            OOPathChunk<V> chunk = new OOPathChunk<V>( map );
            builder.chunks.add(chunk);
            return new OOPathChunkBuilder<S, V>( builder, chunk );
        }

        public OOPathChunkBuilder<S, T> filter( Variable<T> var, Predicate1<T> predicate ) {
            return filter( new Expr1ViewItemImpl<T>( var, predicate ) );
        }

        public <U> OOPathChunkBuilder<S, T> filter(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
            return filter( new Expr2ViewItemImpl<T, U>( var1, var2, predicate ) );
        }

        public OOPathChunkBuilder<S, T> filter( ExprViewItem<T> expr ) {
            chunk.setExpr( expr );
            return this;
        }

        @Override
        public ViewItem<T> get() {
            return new OOPathViewItem<S, T>( builder.source, builder.chunks );
        }
    }
}
