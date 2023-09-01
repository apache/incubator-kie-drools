package org.drools.core.common;


import org.drools.base.common.NetworkNode;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * An interface for node memories implementation
 */
public interface NodeMemories {

    <T extends Memory> T getNodeMemory(MemoryFactory<T> node, ReteEvaluator reteEvaluator);

    void clearNodeMemory( MemoryFactory node );

    void clear();

    /**
     * Peeks at the content of the node memory for the given
     * node ID. This method has no side effects, so if the
     * given memory slot for the given node ID is null, it
     * will return null.
     *
     * @param memoryId
     * @return
     */
    Memory peekNodeMemory( int memoryId );

    default Memory peekNodeMemory(NetworkNode node) {
        return node instanceof MemoryFactory ? peekNodeMemory(((MemoryFactory)node).getMemoryId()) : null;
    }

    /**
     * Returns the number of positions in this memory
     *
     * @return
     */
    int length();

    void resetAllMemories(StatefulKnowledgeSession session);
}
