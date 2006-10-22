package org.drools.util;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Provides a abstract base implementation that an object can extend so that it can be used in a LinkedList.
 * 
 * @see LinkedList
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class AbstractBaseLinkedListNode
    implements
    LinkedListNode {

    private static final long serialVersionUID = -3926700105253864146L;

    private LinkedListNode previous;

    private LinkedListNode next;

    /**
     * Empty Constructor
     */
    public AbstractBaseLinkedListNode() {
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LinkedListNode#getNext()
     */
    public LinkedListNode getNext() {
        return this.next;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LinkedListNode#setNext(org.drools.reteoo.LinkedListNode)
     */
    public void setNext(final LinkedListNode next) {
        this.next = next;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LinkedListNode#getPrevious()
     */
    public LinkedListNode getPrevious() {
        return this.previous;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.LinkedListNode#setPrevious(org.drools.reteoo.LinkedListNode)
     */
    public void setPrevious(final LinkedListNode previous) {
        this.previous = previous;
    }
}
