package org.drools.mvelcompiler.phase3;

import java.util.List;

import com.github.javaparser.ast.Node;

public class FlattenedExpressionResult {

    private Node firstNode;
    private List<Node> otherNodes;

    public FlattenedExpressionResult(Node firstNode, List<Node> otherNodes) {
        this.firstNode = firstNode;
        this.otherNodes = otherNodes;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public List<Node> getOtherNodes() {
        return otherNodes;
    }
}
