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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphJsonGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GraphJsonGenerator.class);

    private GraphJsonGenerator() {
        // Creating instances of this class is not allowed.
    }

    public static String generateJson(Graph graph) {
        Map<String, List<Map<String, String>>> map = convertToMap(graph);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Map<String, List<Map<String, String>>> convertToMap(Graph graph) {
        Map<String, List<Map<String, String>>> map = new HashMap<>();
        List<Map<String, String>> nodes = new ArrayList<>();
        List<Map<String, String>> edges = new ArrayList<>();
        map.put("NODES", nodes);
        map.put("EDGES", edges);
        for (Node node : graph.getNodeMap().values()) {
            Map<String, String> nodeMap = new HashMap<>();
            nodeMap.put("id", node.getId());
            nodeMap.put("type", "node");
            nodeMap.put("label", node.getRuleName());
            nodes.add(nodeMap);

            for (Link outgoingLink : node.getOutgoingLinks()) {
                Map<String, String> edgeMap = new HashMap<>();
                edgeMap.put("id", "edge-" + node.getId() + "-" + outgoingLink.getTarget().getId());
                edgeMap.put("type", "edge");
                edgeMap.put("source", node.getId());
                edgeMap.put("target", outgoingLink.getTarget().getId());
                switch (outgoingLink.getReactivityType()) {
                    case POSITIVE:
                        edgeMap.put("edgeStyle", "solid");
                        break;
                    case NEGATIVE:
                        edgeMap.put("edgeStyle", "dashed");
                        break;
                    case UNKNOWN:
                        edgeMap.put("edgeStyle", "dotted");
                        break;
                    default:
                        logger.warn("Unexpected reactivity type: {}", outgoingLink.getReactivityType());
                        edgeMap.put("edgeStyle", "solid");
                }
                edges.add(edgeMap);
            }
        }
        return map;
    }
}
