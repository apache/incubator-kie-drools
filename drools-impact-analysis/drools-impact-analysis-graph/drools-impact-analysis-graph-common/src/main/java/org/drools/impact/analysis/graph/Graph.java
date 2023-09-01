package org.drools.impact.analysis.graph;

import java.util.Map;

public class Graph {

    private Map<String, Node> nodeMap;

    public Graph(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    /**
     * Set all nodes status to NONE
     */
    void resetNodeStatus() {
        this.nodeMap.values().stream().forEach(node -> node.setStatus(Node.Status.NONE));
    }

    @Override
    public String toString() {
        return "Graph [nodeMap=" + nodeMap + "]";
    }

}
