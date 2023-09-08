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
package org.drools.impact.analysis.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.impact.analysis.graph.Node.Status;

public class ImpactAnalysisHelper {

    private LinkFilter linkFilter = LinkFilter.ALL;

    public ImpactAnalysisHelper() {}

    // will be deprecated
    public ImpactAnalysisHelper(boolean positiveOnly) {
        if (positiveOnly) {
            this.linkFilter = LinkFilter.POSITIVE;
        } else {
            this.linkFilter = LinkFilter.ALL;
        }
    }

    public ImpactAnalysisHelper(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

    /**
     * Forward graph of impact analysis. Collect impacted nodes from a changed node.
     * Set changedNode status to Status.CHANGED and impacted nodes status to Status.IMPACTED
     * @param graph
     * @param name of changedNode (= rule name)
     * @return sub graph which contains only changed node and impacted nodes
     */
    public Graph filterImpactedNodes(Graph graph, String changedNodeName) {
        Node changedNode = graph.getNodeMap().get(changedNodeName);
        if (changedNode == null) {
            throw new RuntimeException("Cannot find a node : name = " + changedNodeName);
        }
        return filterImpactedNodes(graph, changedNode);
    }

    /**
     * Forward graph of impact analysis. Collect impacted nodes from a changed node.
     * Set changedNode status to Status.CHANGED and impacted nodes status to Status.IMPACTED
     * @param graph
     * @param changedNode
     * @return sub graph which contains only changed node and impacted nodes
     */
    public Graph filterImpactedNodes(Graph graph, Node changedNode) {
        graph.resetNodeStatus(); // reset node status implicitly
        Collection<Node> impactedNodes = new HashSet<>();
        collectImpactedNodes(changedNode, impactedNodes);
        changedNode.setStatus(Status.CHANGED);

        Map<String, Node> nodeMap = graph.getNodeMap();
        Map<String, Node> subMap = getSubMap(nodeMap, impactedNodes);

        return new Graph(subMap);
    }

    private void collectImpactedNodes(Node changedNode, Collection<Node> impactedNodes) {
        changedNode.setStatus(Status.IMPACTED);
        impactedNodes.add(changedNode);
        changedNode.getOutgoingLinks().stream()
                   .filter(link -> linkFilter.accept(link.getReactivityType()))
                   .map(Link::getTarget)
                   .filter(node -> !impactedNodes.contains(node))
                   .forEach(node -> collectImpactedNodes(node, impactedNodes));
    }

    private Map<String, Node> getSubMap(Map<String, Node> nodeMap, Collection<Node> impactedNodes) {
        Map<String, Node> subMap = new HashMap<>();
        impactedNodes.stream().map(node -> node.getId()).forEach(id -> subMap.put(id, nodeMap.get(id)));
        return subMap;
    }

    /**
     * Backward graph of impact analysis. Collect impacting nodes from a target node.
     * Set targetNode status to Status.IMPACTED and impacting nodes status to Status.IMPACTING
     * @param graph
     * @param name of targetNode (= rule name)
     * @return sub graph which contains only target node and impacting nodes
     */
    public Graph filterImpactingNodes(Graph graph, String targetNodeName) {
        Node targetNode = graph.getNodeMap().get(targetNodeName);
        if (targetNode == null) {
            throw new RuntimeException("Cannot find a node : name = " + targetNodeName);
        }
        return filterImpactingNodes(graph, targetNode);
    }

    /**
     * Backward graph of impact analysis. Collect impacting nodes from a target node.
     * Set targetNode status to Status.TARGET and impacting nodes status to Status.IMPACTING
     * @param graph
     * @param targetNode
     * @return sub graph which contains only target node and impacting nodes
     */
    public Graph filterImpactingNodes(Graph graph, Node targetNode) {
        graph.resetNodeStatus(); // reset node status implicitly
        Collection<Node> impactingNodes = new HashSet<>();
        collectImpactingNodes(targetNode, impactingNodes);
        targetNode.setStatus(Status.TARGET);

        Map<String, Node> nodeMap = graph.getNodeMap();
        Map<String, Node> subMap = getSubMap(nodeMap, impactingNodes);

        return new Graph(subMap);
    }

    private void collectImpactingNodes(Node targetNode, Collection<Node> impactingNodes) {
        targetNode.setStatus(Status.IMPACTING);
        impactingNodes.add(targetNode);
        targetNode.getIncomingLinks().stream()
                   .filter(link -> linkFilter.accept(link.getReactivityType()))
                   .map(Link::getSource)
                   .filter(node -> !impactingNodes.contains(node))
                   .forEach(node -> collectImpactingNodes(node, impactingNodes));
    }
}
