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

package org.drools.core.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Provides a abstract base implementation that an object can extend so that it can be used in a LinkedList.
 *
 * @see LinkedList
 *
 */
public class AbstractBaseLinkedListNode
    implements
    LinkedListNode {

    private static final long serialVersionUID = 510l;

    private LinkedListNode    previous;

    private LinkedListNode    next;

    /**
     * Empty Constructor
     */
    public AbstractBaseLinkedListNode() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        previous    = (LinkedListNode)in.readObject();
        next    = (LinkedListNode)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(previous);
        out.writeObject(next);
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
