package org.jbpm.serverless.workflow;

import org.jbpm.process.core.Context;
import org.jbpm.workflow.core.NodeContainer;
import org.kie.api.definition.process.Node;

public class TestNodeContainer implements NodeContainer {
    @Override
    public void addNode(Node node) {

    }

    @Override
    public void removeNode(Node node) {

    }

    @Override
    public Context resolveContext(String contextId, Object param) {
        return null;
    }

    @Override
    public Node internalGetNode(long id) {
        return null;
    }

    @Override
    public Node[] getNodes() {
        return new Node[0];
    }

    @Override
    public Node getNode(long id) {
        return null;
    }
}
