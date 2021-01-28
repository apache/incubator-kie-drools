/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.impact.analysis.graph.Node.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactAnalysisHelper {

    private static Logger logger = LoggerFactory.getLogger(ImpactAnalysisHelper.class);

    public Graph filterImpactedNodes(Graph graph, Node changedNode) {

        Collection<Node> impactedNodes = new HashSet<Node>();
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
                   .map(Link::getTarget)
                   .filter(node -> !impactedNodes.contains(node))
                   .forEach(node -> collectImpactedNodes(node, impactedNodes));
    }

    private Map<String, Node> getSubMap(Map<String, Node> nodeMap, Collection<Node> impactedNodes) {
        Map<String, Node> subMap = new HashMap<>();
        impactedNodes.stream().map(node -> node.getId()).forEach(id -> subMap.put(id, nodeMap.get(id)));
        return subMap;
    }
}
