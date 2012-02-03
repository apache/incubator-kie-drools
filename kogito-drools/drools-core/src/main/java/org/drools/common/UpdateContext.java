package org.drools.common;

import java.util.Stack;

public class UpdateContext {

    private Stack<BaseNode> visitedNodes = new Stack<BaseNode>();

    public void startVisitNode(BaseNode baseNode) {
        visitedNodes.push(baseNode);
    }

    public void endVisit() {
        visitedNodes.pop();
    }

    public boolean isVisiting(BaseNode baseNode) {
        return !visitedNodes.isEmpty() && visitedNodes.peek().equals(baseNode);
    }
}
