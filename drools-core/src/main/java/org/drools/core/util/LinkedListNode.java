package org.drools.core.util;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 *
 * @see LinkedList
 */
public interface LinkedListNode<T extends LinkedListNode> extends Entry<T> {

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    public T getPrevious();

    /**
     * Sets the previous node
     * @param previous
     *      The previous LinkedListNode
     */
    public void setPrevious(T previous);

    void nullPrevNext();

}
