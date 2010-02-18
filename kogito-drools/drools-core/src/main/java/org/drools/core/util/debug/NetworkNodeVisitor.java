package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;

public interface NetworkNodeVisitor {

    void visit(NetworkNode parent,
               Stack<NetworkNode> nodeStack,
               StatefulKnowledgeSessionInfo info);

}
