package org.drools.core.common;

import java.util.ArrayDeque;
import java.util.Deque;

public class UpdateContext {

    private Deque<BaseNode> visitedNodes = new ArrayDeque<>();

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
