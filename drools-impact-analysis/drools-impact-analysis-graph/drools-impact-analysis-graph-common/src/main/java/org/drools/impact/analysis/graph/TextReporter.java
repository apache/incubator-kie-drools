/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextReporter {

    public static String INDENT = "  ";

    private TextReporter() {}

    public static String toFlatText(Graph g) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = g.getNodeMap().keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keyList) {
            Node node = g.getNodeMap().get(key);
            String ruleName = node.getRuleName();
            sb.append(ruleName);

            String mark = "";
            if (node.getStatus() == Node.Status.CHANGED) {
                mark = "[*]";
            } else if (node.getStatus() == Node.Status.IMPACTED) {
                mark = "[+]";
            }
            sb.append(mark);

            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public static String toHierarchyText(Graph g) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = g.getNodeMap().keySet().stream().sorted().collect(Collectors.toList());
        Set<Node> pickedNodeSet = new HashSet<>();
        for (String key : keyList) {
            Node node = g.getNodeMap().get(key);
            if (pickedNodeSet.contains(node)) {
                continue;
            }
            addNode(node, "", pickedNodeSet, sb);
        }
        return sb.toString();
    }

    private static void addNode(Node node, String indent, Set<Node> pickedNodeSet, StringBuilder sb) {
        sb.append(indent);

        String ruleName = node.getRuleName();
        sb.append(ruleName);

        String mark = "";
        if (node.getStatus() == Node.Status.CHANGED) {
            mark = "[*]";
        } else if (node.getStatus() == Node.Status.IMPACTED) {
            mark = "[+]";
        }
        sb.append(mark);

        sb.append(System.lineSeparator());

        pickedNodeSet.add(node);

        for (Link link : node.getOutgoingLinks()) {

            Node target = link.getTarget();
            if (pickedNodeSet.contains(target)) {
                addNodeWithoutCircular(target, indent + INDENT, sb);
            } else {
                addNode(target, indent + INDENT, pickedNodeSet, sb);
            }
        }
    }

    private static void addNodeWithoutCircular(Node node, String indent, StringBuilder sb) {
        sb.append(indent);
        sb.append("(" + node.getRuleName() + ")" + System.lineSeparator());
    }
}
