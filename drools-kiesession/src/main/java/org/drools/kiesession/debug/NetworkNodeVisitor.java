package org.drools.kiesession.debug;

import java.util.Collection;

import org.drools.base.common.NetworkNode;

public interface NetworkNodeVisitor {

    void visit(NetworkNode parent,
            Collection<NetworkNode> nodeStack,
               StatefulKnowledgeSessionInfo info);

}
