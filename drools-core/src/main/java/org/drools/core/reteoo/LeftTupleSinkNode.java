package org.drools.core.reteoo;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 * 
 * @see LeftTupleSinkNodeList
 */
public interface LeftTupleSinkNode extends LeftTupleSink {

    /**
     * Returns the next node
     * @return
     *      The next LinkedListNode
     */
    LeftTupleSinkNode getNextLeftTupleSinkNode();

    /**
     * Sets the next node 
     * @param next
     *      The next LinkedListNode
     */
    void setNextLeftTupleSinkNode(LeftTupleSinkNode next);

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    LeftTupleSinkNode getPreviousLeftTupleSinkNode();

    /**
     * Sets the previous node 
     * @param previous
     *      The previous LinkedListNode
     */
    void setPreviousLeftTupleSinkNode(LeftTupleSinkNode previous);
}
