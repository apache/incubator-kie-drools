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

package org.drools.util;

/**
 * BaseMultiLinkedListNode
 * A base implementation for the MultiLinkedListNode that helps to
 * manage object references.
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class BaseMultiLinkedListNode extends AbstractBaseLinkedListNode
    implements
    MultiLinkedListNode {

    private MultiLinkedListNode child = null;
    private LinkedList          list  = null;

    public BaseMultiLinkedListNode() {
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.util.MultiLinkedListNode#getChild()
     */
    public MultiLinkedListNode getChild() {
        return this.child;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.util.MultiLinkedListNode#setChild(org.drools.util.LinkedListNode)
     */
    public void setChild(final MultiLinkedListNode child) {
        this.child = child;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.util.MultiLinkedListNode#getLinkedList()
     */
    public LinkedList getLinkedList() {
        return this.list;
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.util.MultiLinkedListNode#setLinkedList(org.drools.util.LinkedList)
     */
    public void setLinkedList(final LinkedList list) {
        this.list = list;
    }

}
