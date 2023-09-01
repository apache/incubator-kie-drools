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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphCollapsionHelper {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(.*)_\\d*");

    private LinkFilter linkFilter = LinkFilter.ALL;

    public GraphCollapsionHelper() {}

    // will be deprecated
    public GraphCollapsionHelper(boolean positiveOnly) {
        if (positiveOnly) {
            this.linkFilter = LinkFilter.POSITIVE;
        } else {
            this.linkFilter = LinkFilter.ALL;
        }
    }

    public GraphCollapsionHelper(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

    /**
     * Spreadsheet generates this rule name format : <RuleNamePrefix>_<Number> (e.g. OrderEvaluation_11)
     * This method groups rules based on the RuleNamePrefix so collapses the graph based to see its overview
     * @param graph
     * @return collapsed graph
     */
    public Graph collapseWithRuleNamePrefix(Graph graph) {
        Map<String, Node> nodeMap = graph.getNodeMap();

        Map<String, Set<Node>> groupedNodeMap = groupWithRuleNamePrefix(nodeMap);
        Map<String, Node> collapsedNodeMap = collapseGroupedNodes(groupedNodeMap);

        return new Graph(collapsedNodeMap);
    }

    private Map<String, Node> collapseGroupedNodes(Map<String, Set<Node>> groupedNodeMap) {
        Map<String, Node> collapsedNodeMap = new HashMap<>();
        for (String prefixedKey : groupedNodeMap.keySet()) {
            Set<Node> nodeSet = groupedNodeMap.get(prefixedKey);
            Node tmpNode = nodeSet.iterator().next(); // pick any one node
            String packageName = tmpNode.getPackageName();
            String ruleName = tmpNode.getRuleName();
            String ruleNamePrefix = getPrefix(ruleName);
            Node collapsedNode = new Node(packageName, ruleNamePrefix);
            collapsedNodeMap.put(prefixedKey, collapsedNode); // prefixedKey = packageName.ruleNamePrefix
        }
        for (String prefixedKey : groupedNodeMap.keySet()) {
            Set<Node> nodeSet = groupedNodeMap.get(prefixedKey);
            nodeSet.stream().flatMap(node -> node.getOutgoingLinks().stream()).forEach(link -> {
                ReactivityType type = link.getReactivityType();
                Node sourceCollapsedNode = collapsedNodeMap.get(prefixedKey);
                Node target = link.getTarget();
                Node targetCollapsedNode = collapsedNodeMap.get(getPrefix(target.getFqdn()));
                if (linkFilter.accept(type)) {
                    Node.linkNodes(sourceCollapsedNode, targetCollapsedNode, type);
                }
            });
        }
        return collapsedNodeMap;
    }

    private Map<String, Set<Node>> groupWithRuleNamePrefix(Map<String, Node> nodeMap) {
        Map<String, Set<Node>> groupedNodeMap = new HashMap<>();
        nodeMap.keySet().stream().forEach(key -> {
            String prefixedKey = getPrefix(key); // prefixedKey is packageName.ruleNamePrefix
            groupedNodeMap.computeIfAbsent(prefixedKey, k -> new HashSet<>()).add(nodeMap.get(key));
        });
        return groupedNodeMap;
    }

    /*
     * CustomerCheck_1 -> CustomerCheck
     * mypkg.CustomerCheck_1 -> mypkg.CustomerCheck
     */
    private String getPrefix(String key) {
        Matcher m = PREFIX_PATTERN.matcher(key);
        if (m.find()) {
            return m.group(1);
        } else {
            return key;
        }
    }

}
