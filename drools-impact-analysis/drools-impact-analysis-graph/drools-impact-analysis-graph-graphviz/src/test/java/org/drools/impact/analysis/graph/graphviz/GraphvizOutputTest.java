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

package org.drools.impact.analysis.graph.graphviz;

import java.util.HashMap;
import java.util.Map;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.model.Rule;
import org.junit.Test;

/**
 * 
 * This class is to test from drools-impact-analysis-graph-common Graph to graphviz output.
 * 
 * If you want to test from DRL (involving parser), use drools-impact-analysis-itests
 *
 */
public class GraphvizOutputTest {

    @Test
    public void testSimpleGraph() {
        Node node1 = new Node(new Rule("org.example", "rule1", "dummy"));
        Node node2 = new Node(new Rule("org.example", "rule2", "dummy"));
        Node node3 = new Node(new Rule("org.example", "rule3", "dummy"));
        Node node4 = new Node(new Rule("org.example", "rule4", "dummy"));
        Node node5 = new Node(new Rule("org.example", "rule5", "dummy"));

        Node.linkNodes(node1, node2, Link.Type.POSITIVE);
        Node.linkNodes(node1, node3, Link.Type.NEGATIVE);
        Node.linkNodes(node2, node4, Link.Type.UNKNOWN);
        Node.linkNodes(node3, node5, Link.Type.POSITIVE);

        Map<String, Node> nodeMap = new HashMap<>();
        nodeMap.put(node1.getFqdn(), node1);
        nodeMap.put(node2.getFqdn(), node2);
        nodeMap.put(node3.getFqdn(), node3);
        nodeMap.put(node4.getFqdn(), node4);
        nodeMap.put(node5.getFqdn(), node5);

        Graph graph = new Graph(nodeMap);

        GraphImageGenerator generator = new GraphImageGenerator("simple");
        generator.generatePng(graph);
    }
}
