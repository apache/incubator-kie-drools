package org.drools.mvelcompiler.phase3;

import java.util.List;

import com.github.javaparser.ast.Node;

public class FlattenedAssignmentResult extends FlattenedExpressionResult {

    private String assignmentName;

    public FlattenedAssignmentResult(Node firstNode, List<Node> otherNodes, String assignmentName) {
        super(firstNode, otherNodes);
        this.assignmentName = assignmentName;
    }
}
