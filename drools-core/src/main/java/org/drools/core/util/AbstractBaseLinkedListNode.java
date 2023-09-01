package org.drools.core.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Provides a abstract base implementation that an object can extend so that it can be used in a LinkedList.
 *
 * @see LinkedList
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="linked-list")
public abstract class AbstractBaseLinkedListNode<T extends LinkedListNode<T>>
    implements
    LinkedListNode<T> {

    private static final long serialVersionUID = 510l;

    private T    previous;

    private T    next;

    /**
     * Empty Constructor
     */
    public AbstractBaseLinkedListNode() {
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LinkedListNode#remove()
     */
    public T getNext() {
        return this.next;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LinkedListNode#setNext(org.kie.reteoo.LinkedListNode)
     */
    public void setNext(final T next) {
        this.next = next;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LinkedListNode#getPrevious()
     */
    public T getPrevious() {
        return this.previous;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LinkedListNode#setPrevious(org.kie.reteoo.LinkedListNode)
     */
    public void setPrevious(final T previous) {
        this.previous = previous;
    }

    public void nullPrevNext() {
        previous = null;
        next = null;
    }
}
