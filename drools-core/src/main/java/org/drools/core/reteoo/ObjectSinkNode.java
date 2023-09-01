package org.drools.core.reteoo;

import org.drools.base.common.RuleBasePartitionId;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 * 
 * @see LeftTupleSinkNodeList
 */
public interface ObjectSinkNode
    extends
    ObjectSink {

    /**
     * Returns the next node
     * @return
     *      The next LinkedListNode
     */

    ObjectSinkNode getNextObjectSinkNode();

    /**
     * Sets the next node 
     * @param next
     *      The next LinkedListNode
     */
    void setNextObjectSinkNode(ObjectSinkNode next);

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    ObjectSinkNode getPreviousObjectSinkNode();

    /**
     * Sets the previous node 
     * @param previous
     *      The previous LinkedListNode
     */
    void setPreviousObjectSinkNode(ObjectSinkNode previous);

    void setPartitionIdWithSinks( RuleBasePartitionId partitionId );
}
