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

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;

import java.util.Iterator;

public class GraphImpl<T> implements Graph<T>, Iterable<GraphNode<T>> {
    GraphStore<T> graphStore;

    public GraphImpl(GraphStore<T> graphStore) {
        this.graphStore = graphStore;
    }

    protected int idCounter;


    public GraphNode<T> addNode() {
        return graphStore.addNode();
    }

    public GraphNode<T> removeNode(int id) {
        return graphStore.removeNode(id);
    }

    @Override
    public GraphNode<T> getNode(int id) {
        return graphStore.getNode(id);
    }

    @Override
    public int size() {
        return graphStore.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return graphStore.iterator();
    }
}
