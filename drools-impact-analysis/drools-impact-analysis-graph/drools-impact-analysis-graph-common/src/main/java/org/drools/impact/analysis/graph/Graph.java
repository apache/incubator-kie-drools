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

    @Override
    public String toString() {
        return "Graph [nodeMap=" + nodeMap + "]";
    }

}
