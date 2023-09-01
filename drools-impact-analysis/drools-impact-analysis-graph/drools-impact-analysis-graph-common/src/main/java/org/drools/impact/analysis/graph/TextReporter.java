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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.impact.analysis.graph.Node.Status;

public class TextReporter {

    public static String INDENT = "  ";

    private TextReporter() {}

    public static String toFlatText(Graph g) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = g.getNodeMap().keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keyList) {
            Node node = g.getNodeMap().get(key);
            appendNodeText(sb, node);
        }
        return sb.toString();
    }

    private static void appendNodeText(StringBuilder sb, Node node) {
        String ruleName = node.getRuleName();
        sb.append(ruleName);
        sb.append(statusToMark(node.getStatus()));
        sb.append(System.lineSeparator());
    }

    private static String statusToMark(Status status) {
        switch (status) {
            case CHANGED:
                return "[*]";
            case IMPACTED:
                return "[+]";
            case TARGET:
                return "[@]";
            case IMPACTING:
                return "[!]";
            case NONE:
            default:
                return "";
        }
    }

    /**
     * Render forward graph of impact analysis with hierarchy text
     * @param g
     * @return text
     */
    public static String toHierarchyText(Graph g) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = g.getNodeMap().keySet().stream().sorted().collect(Collectors.toList());
        Set<Node> pickedNodeSet = new HashSet<>();
        for (String key : keyList) {
            Node node = g.getNodeMap().get(key);
            if (pickedNodeSet.contains(node)) {
                continue;
            }
            addNode(g, node, "", pickedNodeSet, sb);
        }
        return sb.toString();
    }

    private static void addNode(Graph g, Node node, String indent, Set<Node> pickedNodeSet, StringBuilder sb) {
        sb.append(indent);
        appendNodeText(sb, node);

        pickedNodeSet.add(node);

        for (Link link : node.getOutgoingLinks()) {
            Node target = link.getTarget();
            if (!g.getNodeMap().containsValue(target)) {
                continue;
            }
            if (pickedNodeSet.contains(target)) {
                addNodeWithoutCircular(target, indent + INDENT, sb);
            } else {
                addNode(g, target, indent + INDENT, pickedNodeSet, sb);
            }
        }
    }

    private static void addNodeWithoutCircular(Node node, String indent, StringBuilder sb) {
        sb.append(indent);
        sb.append("(" + node.getRuleName() + ")" + System.lineSeparator());
    }
}
