package org.drools.mvelcompiler.phase2;

import java.util.List;

import com.github.javaparser.ast.Node;
import org.drools.mvelcompiler.phase3.TypedExpression;

public class FlattenExpressionResult {

    private TypedExpression firstNode;
    private List<Node> otherNodes;

    public FlattenExpressionResult(TypedExpression firstNode, List<Node> otherNodes) {
        this.firstNode = firstNode;
        this.otherNodes = otherNodes;
    }

    public TypedExpression getFirstNode() {
        return firstNode;
    }

    public List<Node> getOtherNodes() {
        return otherNodes;
    }
}
