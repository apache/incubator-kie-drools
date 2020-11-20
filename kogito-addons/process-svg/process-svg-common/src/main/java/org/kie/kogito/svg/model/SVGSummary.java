/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.svg.model;

import java.util.HashMap;
import java.util.Map;

public class SVGSummary {

    private Map<String, NodeSummary> nodes = new HashMap<>();

    public void addNode(NodeSummary node) {
        String nodeId = node.getNodeId();
        if (nodeId == null) {
            throw new IllegalArgumentException("Node id cannot be null.");
        }
        nodes.put(node.getNodeId(), node);
    }

    public NodeSummary getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public Map<String, NodeSummary> getNodesMap() {
        return nodes;
    }
}