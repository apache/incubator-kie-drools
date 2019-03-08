package org.drools.mvelcompiler.phase2;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;

public class FlattenedExpressionResult {

    private Node firstNode;
    private List<Node> otherNodes;
    private Expression expression;

    public FlattenedExpressionResult(Node firstNode, List<Node> otherNodes, Expression expression) {
        this.firstNode = firstNode;
        this.otherNodes = otherNodes;
        this.expression = expression;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public List<Node> getOtherNodes() {
        return otherNodes;
    }

    public Expression getExpression() {
        return expression;
    }
}
