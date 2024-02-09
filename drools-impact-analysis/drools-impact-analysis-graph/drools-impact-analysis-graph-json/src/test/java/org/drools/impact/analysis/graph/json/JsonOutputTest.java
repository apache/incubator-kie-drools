/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.impact.analysis.graph.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.model.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is to test from drools-impact-analysis-graph-common Graph to json output.
 * <p>
 * If you want to test from DRL (involving parser), use drools-impact-analysis-itests
 */
public class JsonOutputTest {

    private static final String EXPECTED_JSON =
            """
                    {
                      "NODES":[
                        {
                          "id":"org.example.rule1",
                          "label":"rule1",
                          "type":"node"
                        },
                        {
                          "id":"org.example.rule5",
                          "label":"rule5",
                          "type":"node"
                        },
                        {
                          "id":"org.example.rule4",
                          "label":"rule4",
                          "type":"node"
                        },
                        {
                          "id":"org.example.rule3",
                          "label":"rule3",
                          "type":"node"
                        },
                        {
                          "id":"org.example.rule2",
                          "label":"rule2",
                          "type":"node"
                        }
                      ],
                      "EDGES":[
                        {
                          "id":"edge-org.example.rule1-org.example.rule2",
                          "source":"org.example.rule1",
                          "type":"edge",
                          "edgeStyle":"solid",
                          "target":"org.example.rule2"
                        },
                        {
                          "id":"edge-org.example.rule1-org.example.rule3",
                          "source":"org.example.rule1",
                          "type":"edge",
                          "edgeStyle":"dashed",
                          "target":"org.example.rule3"
                        },
                        {
                          "id":"edge-org.example.rule3-org.example.rule5",
                          "source":"org.example.rule3",
                          "type":"edge",
                          "edgeStyle":"solid",
                          "target":"org.example.rule5"
                        },
                        {
                          "id":"edge-org.example.rule2-org.example.rule4",
                          "source":"org.example.rule2",
                          "type":"edge",
                          "edgeStyle":"dotted",
                          "target":"org.example.rule4"
                        }
                      ]
                    }
                    """;

    @Test
    public void generate_simpleGraph() throws JsonProcessingException {
        Graph graph = createSimpleGraph();
        String actualJson = GraphJsonGenerator.generateJson(graph);
        assertJson(actualJson, EXPECTED_JSON);
    }

    private static void assertJson(String actualJson, String expectedJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, String>>> actualMap = objectMapper.readValue(actualJson, Map.class);
        Map<String, List<Map<String, String>>> expectedMap = objectMapper.readValue(expectedJson, Map.class);

        List<Map<String, String>> actualNodes = actualMap.get("NODES");
        List<Map<String, String>> expectedNodes = expectedMap.get("NODES");
        assertThat(actualNodes).containsExactlyInAnyOrderElementsOf(expectedNodes);

        List<Map<String, String>> actualEdges = actualMap.get("EDGES");
        List<Map<String, String>> expectedEdges = expectedMap.get("EDGES");
        assertThat(actualEdges).containsExactlyInAnyOrderElementsOf(expectedEdges);
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

        return new Graph(nodeMap);
    }
}
