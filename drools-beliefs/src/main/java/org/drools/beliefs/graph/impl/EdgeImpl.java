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

import org.drools.beliefs.graph.Edge;
import org.drools.beliefs.graph.GraphNode;

public class EdgeImpl implements Edge {

    private GraphNode inGraphNode;

    private GraphNode outGraphNode;

    @Override
    public GraphNode getInGraphNode() {
        return inGraphNode;
    }

    @Override
    public GraphNode getOutGraphNode() {
        return outGraphNode;
    }

    public void setInGraphNode(GraphNode inGraphNode) {
        this.inGraphNode = inGraphNode;
        ((GraphNodeImpl) inGraphNode).addInEdge(this);
    }

    public void setOutGraphNode(GraphNode outGraphNode) {
        this.outGraphNode = outGraphNode;
        ((GraphNodeImpl) outGraphNode).addOutEdge(this);
    }

    @Override
    public String toString() {
        return "EdgeImpl{" +
               "inVertex=" + inGraphNode.getId() +
               ", outVertex=" + outGraphNode.getId() +
               '}';
    }
}
