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
 * MultiLinkedList
 * A linked list where each node has a reference to the list itself and to a 
 * child node.
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 14/02/2006
 */
public class MultiLinkedList extends LinkedList {

    public MultiLinkedList() {
    }

    /**
     * Add a <code>MultiLinkedListNode</code> to the list. 
     * If the <code>MultiLinkedList</code> is empty then the first and 
     * last nodes are set to the added node.
     * 
     * @param node
     *      The <code>LinkedListNode</code> to be added
     */
    public void add(final MultiLinkedListNode node) {
        super.add( node );
        node.setOuterList( this );
    }

    /**
     * Removes a <code>MultiLinkedListNode</code> from the list. 
     * This works by attach the previous reference to the child reference.
     * When the node to be removed is the first node it calls <code>removeFirst()</code>. 
     * When the node to be removed is the last node
     * it calls <code>removeLast()</code>.
     * 
     * @param node
     *      The <code>LinkedListNode</code> to be removed.
     */
    public void remove(final MultiLinkedListNode node) {
        super.remove( node );
        node.setOuterList( null );
    }
}
