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
package org.drools.impact.analysis.graph.graphviz;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import guru.nidi.graphviz.engine.Graphviz;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.model.Rule;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;

/**
 * 
 * This class is to test from drools-impact-analysis-graph-common Graph to graphviz output.
 * 
 * If you want to test from DRL (involving parser), use drools-impact-analysis-itests
 *
 */
public class GraphvizOutputTest {

    @After
    public void tearDown() {
        Graphviz.releaseEngine();
    }

    @Test
    public void generate_simpleGraph() throws URISyntaxException {
        Graph graph = createSimpleGraph();

        GraphImageGenerator generator = new GraphImageGenerator("simple");
        String filePath = generator.generateDot(graph);
        File actual = new File(filePath);
        File expected = new File(this.getClass().getResource("simple.dot").toURI());
        assertThat(linesOf(actual)).containsExactlyInAnyOrderElementsOf(linesOf(expected));

        generator.generateSvg(graph); // no assertion because graphviz-java may fail to render SVG depending on environments
        generator.generatePng(graph); // no assertion because graphviz-java may fail to render PNG depending on environments
    }

    private Graph createSimpleGraph() {
        Node node1 = new Node(new Rule("org.example", "rule1", "example"));
        Node node2 = new Node(new Rule("org.example", "rule2", "example"));
        Node node3 = new Node(new Rule("org.example", "rule3", "example"));
        Node node4 = new Node(new Rule("org.example", "rule4", "example"));
        Node node5 = new Node(new Rule("org.example", "rule5", "example"));

        Node.linkNodes(node1, node2, ReactivityType.POSITIVE);
        Node.linkNodes(node1, node3, ReactivityType.NEGATIVE);
        Node.linkNodes(node2, node4, ReactivityType.UNKNOWN);
        Node.linkNodes(node3, node5, ReactivityType.POSITIVE);

        Map<String, Node> nodeMap = new HashMap<>();
        nodeMap.put(node1.getFqdn(), node1);
        nodeMap.put(node2.getFqdn(), node2);
        nodeMap.put(node3.getFqdn(), node3);
        nodeMap.put(node4.getFqdn(), node4);
        nodeMap.put(node5.getFqdn(), node5);

        Graph graph = new Graph(nodeMap);
        return graph;
    }

    @Test
    public void generate_simulateEngineFailure() throws URISyntaxException {
        Graph graph = createSimpleGraph();

        GraphImageGenerator generator = GraphImageGenerator.getGraphImageGeneratorWithErrorGraphvizEngine("simple"); // initialize with a failing render engine without fallback
        String filePathDot = generator.generateDot(graph);
        File actual = new File(filePathDot);
        File expected = new File(this.getClass().getResource("simple.dot").toURI());
        assertThat(linesOf(actual)).containsExactlyInAnyOrderElementsOf(linesOf(expected)); // DOT works as usual

        String filePathSvg = generator.generateSvg(graph); // Raises WARN but this test doesn't fail
        assertThat(filePathSvg).isNull();

        String filePathPng = generator.generatePng(graph); // Raises WARN but this test doesn't fail
        assertThat(filePathPng).isNull();
    }
}
