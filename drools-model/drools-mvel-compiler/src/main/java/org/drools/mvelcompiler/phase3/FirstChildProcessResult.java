package org.drools.mvelcompiler.phase3;

import java.util.List;

import com.github.javaparser.ast.Node;
import org.drools.mvelcompiler.phase4.TypedExpression;

public class FirstChildProcessResult {

    private TypedExpression firstNode;
    private List<Node> otherNodes;

    public FirstChildProcessResult(TypedExpression firstNode, List<Node> otherNodes) {
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
