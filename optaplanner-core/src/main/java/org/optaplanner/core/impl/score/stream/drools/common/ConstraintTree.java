/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNodeType;

final class ConstraintTree<Node_ extends ConstraintGraphNode, Consequence_ extends ConstraintConsequence<Node_>> {

    private final ConstraintSubTree nestedNodes;

    ConstraintTree(Consequence_ consequence) {
        List<ConstraintGraphNode> orderedNodeList = orderNodes(consequence);
        this.nestedNodes = assembleSubTree(orderedNodeList);
        ConstraintGraphNode firstNode = orderedNodeList.get(0);
        if (firstNode.getType() != ConstraintGraphNodeType.FROM) {
            throw new IllegalStateException("Impossible state: First node (" + firstNode + ") is not " +
                    ConstraintGraphNodeType.FROM + " (" + firstNode.getType() + ").");
        }
        ConstraintGraphNode lastNode = orderedNodeList.get(orderedNodeList.size() - 1);
        if (lastNode != consequence.getTerminalNode()) {
            throw new IllegalStateException("Impossible state: Last node (" + lastNode + ") is not terminal (" +
                    consequence.getTerminalNode() + ").");
        }
    }

    private List<ConstraintGraphNode> orderNodes(Consequence_ consequence) {
        // Depth-first search, right parents of join nodes get precedence.
        List<ConstraintGraphNode> nodeList = new ArrayList<>(0);
        Deque<ConstraintGraphNode> unprocessedNodeDeque = new ArrayDeque<>(0);
        unprocessedNodeDeque.add(consequence.getTerminalNode());
        while (!unprocessedNodeDeque.isEmpty()) {
            ConstraintGraphNode node = unprocessedNodeDeque.pollLast();
            nodeList.add(node);
            if (node instanceof ChildNode) {
                List<ConstraintGraphNode> parentNodes = ((ChildNode) node).getParentNodes();
                int parentNodeCount = parentNodes.size();
                if (parentNodeCount == 2) { // Join node.
                    ConstraintGraphNode supposedLeftParent = parentNodes.get(0);
                    ConstraintGraphNode supposedRightParent = parentNodes.get(1);
                    if (supposedLeftParent.getCardinality() < supposedRightParent.getCardinality()) {
                        throw new IllegalStateException("Impossible state: Left join parent (" + supposedLeftParent +
                                ") has lower cardinality (" + supposedLeftParent.getCardinality() + ") than right (" +
                                supposedRightParent + ", " + supposedRightParent.getCardinality() + ")");
                    }
                    unprocessedNodeDeque.add(supposedLeftParent);
                    unprocessedNodeDeque.add(supposedRightParent);
                } else if (parentNodeCount == 1) {
                    unprocessedNodeDeque.add(parentNodes.get(0));
                } else {
                    throw new IllegalStateException("Impossible state: Node (" + node + ") with wrong number of " +
                            "parents (" + parentNodeCount + ").");
                }
            }
        }
        // Reverse order. (Start with left-most From node.)
        Collections.reverse(nodeList);
        return Collections.unmodifiableList(nodeList);
    }

    public ConstraintSubTree getNestedNodes() {
        return nestedNodes;
    }

    private ConstraintSubTree assembleSubTree(List<ConstraintGraphNode> orderedNodeList) {
        List<List<ConstraintGraphNode>> sequentialChunkList = new ArrayList<>(0);
        List<ConstraintGraphNode> unincludedNodeList = new ArrayList<>(0);
        // Separate the ordered list into chunks to be later converted into proper subtrees.
        for (ConstraintGraphNode node : orderedNodeList) {
            if (node.getType() == ConstraintGraphNodeType.FROM || node.getType() == ConstraintGraphNodeType.JOIN) {
                if (!unincludedNodeList.isEmpty()) { // Finish previous chunk.
                    sequentialChunkList.add(unincludedNodeList);
                    unincludedNodeList = new ArrayList<>(0);
                }
            }
            unincludedNodeList.add(node);
        }
        if (!unincludedNodeList.isEmpty()) {
            sequentialChunkList.add(unincludedNodeList);
        }
        // Assemble the chunks into subtrees.
        ConstraintSubTree joinSubTree = null;
        while (!sequentialChunkList.isEmpty()) {
            int chunkCount = sequentialChunkList.size();
            if (joinSubTree == null) {
                switch (chunkCount) {
                    case 2:
                        throw new IllegalStateException("Impossible state: Must have at least three chunks " +
                                "(FROM, FROM, JOIN), but had " + sequentialChunkList + ".");
                    case 1: // This is the only subtree.
                        return new ConstraintSubTree(sequentialChunkList.get(0));
                    default:
                        List<ConstraintGraphNode> currentChunkList = sequentialChunkList.get(2);
                        ConstraintSubTree leftSubTree = new ConstraintSubTree(sequentialChunkList.get(0));
                        ConstraintSubTree rightSubTree = new ConstraintSubTree(sequentialChunkList.get(1));
                        joinSubTree = new ConstraintSubTree(leftSubTree, rightSubTree, currentChunkList);
                        sequentialChunkList = sequentialChunkList.subList(3, sequentialChunkList.size());
                }
            } else {
                if (chunkCount == 1) {
                    throw new IllegalStateException("Impossible state: JOIN must have at least two follow-up chunks " +
                            "(FROM, JOIN), but had " + sequentialChunkList + ".");
                } else {
                    ConstraintSubTree rightSubTree = new ConstraintSubTree(sequentialChunkList.get(0));
                    joinSubTree = new ConstraintSubTree(joinSubTree, rightSubTree, sequentialChunkList.get(1));
                    sequentialChunkList = sequentialChunkList.subList(2, sequentialChunkList.size());
                }
            }
        }
        return joinSubTree;
    }

}
