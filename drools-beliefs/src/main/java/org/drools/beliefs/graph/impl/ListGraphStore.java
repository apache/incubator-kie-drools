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

package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.GraphNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListGraphStore<T> implements GraphStore<T> {
    List<GraphNode<T>> nodes = new ArrayList<GraphNode<T>>();

    List<Integer> oldIds = new ArrayList<Integer>();

    @Override
    public GraphNode<T> addNode() {
        GraphNode<T> v = new GraphNodeImpl<T>(nodes.size());
        nodes.add( v );
        return v;
    }

    @Override
    public GraphNode<T> removeNode(int id) {
        throw new UnsupportedOperationException(ListGraphStore.class.getSimpleName() + " is additive only" );
    }

    @Override
    public GraphNode<T> getNode(int id) {
        return nodes.get( id );
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return nodes.iterator();
    }
}
