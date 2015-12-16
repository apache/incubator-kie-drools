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

package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.GraphNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapGraphStore<T> implements GraphStore<T> {
    protected int idCounter;

    protected Map<Integer, GraphNode<T>> nodes = new HashMap();

    public Map<Integer, GraphNode<T>> getNodes() {
        return nodes;
    }

    public GraphNode<T> addNode() {
        GraphNode<T> v = new GraphNodeImpl<T>(idCounter++);
        nodes.put( v.getId(), v);
        return v;
    }

    public GraphNode<T> removeNode(int id) {
        return nodes.remove( id );
    }

    @Override
    public GraphNode<T> getNode(int id) {
        return nodes.get(id);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return nodes.values().iterator();
    }
}
