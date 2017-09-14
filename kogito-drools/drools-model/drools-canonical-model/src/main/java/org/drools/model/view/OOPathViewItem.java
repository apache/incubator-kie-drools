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
import java.util.List;

import org.drools.model.Source;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public class OOPathViewItem<S, T> implements ViewItem<T> {
    private final Source<S> source;
    private final LinkedList<OOPathChunk<?>> chunks;

    public OOPathViewItem( Source<S> source, LinkedList<OOPathChunk<?>> chunks ) {
        this.source = source;
        this.chunks = chunks;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return (Variable<T>) chunks.getLast().getExpr().getFirstVariable();
    }

    @Override
    public Variable<?>[] getVariables() {
        return chunks.getLast().getExpr().getVariables();
    }

    public List<OOPathChunk<?>> getChunks() {
        return chunks;
    }

    public Source<S> getSource() {
        return source;
    }

    public static class OOPathChunk<T> {
        private ExprViewItem<T> expr;
        private Function1<?, Iterable<T>> map;

        public OOPathChunk() { }

        public OOPathChunk( Function1<?, Iterable<T>> map ) {
            this.map = map;
        }

        public void setExpr( ExprViewItem<T> expr ) {
            this.expr = expr;
        }

        public ExprViewItem<T> getExpr() {
            return expr;
        }
    }
}
