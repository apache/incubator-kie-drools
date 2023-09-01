package org.drools.core.reteoo;

import java.util.function.Consumer;

import org.drools.base.common.NetworkNode;
import org.drools.base.reteoo.BaseTerminalNode;

/**
 * A markup interface for terminal nodes
 */
public interface TerminalNode
    extends
        BaseTerminalNode, NetworkNode, Sink, PathEndNode {

    LeftTupleSource getLeftTupleSource();
    
    LeftTupleSource unwrapTupleSource();

    void visitLeftTupleNodes(Consumer<LeftTupleNode> func);

}
