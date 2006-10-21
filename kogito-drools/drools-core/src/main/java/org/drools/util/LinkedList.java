package org.drools.util;

import java.io.Serializable;

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
 * This is a simple linked linked implementation. Each node must implement </code>LinkedListNode<code> so that it references
 * the node before and after it. This way a node can be removed without having to scan the list to find it. This class
 * does not provide an Iterator implementation as its designed for efficiency and not genericity. There are a number of 
 * ways to iterate the list.
 * <p>
 * Simple iterator:
 * <pre>
 * for ( LinkedListNode node = list.getFirst(); node != null; node =  node.getNext() ) {
 * }
 * </pre>
 * 
 * Iterator that pops the first entry:
 * <pre>
 * for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
 * }
 * </pre>
 *
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class LinkedList
    implements
    Serializable {
    private static final long serialVersionUID = 320;

    private LinkedListNode    firstNode;
    private LinkedListNode    lastNode;

    private int               size;

    private LinkedListIterator iterator;
    
    /**
     * Construct an empty <code>LinkedList</code>
     */
    public LinkedList() {
        this.iterator = new LinkedListIterator();
    }

    /**
     * Add a <code>LinkedListNode</code> to the list. If the <code>LinkedList</code> is empty then the first and 
     * last nodes are set to the added node.
     * 
     * @param node
     *      The <code>LinkedListNode</code> to be added
     */
    public void add(final LinkedListNode node) {
        if ( this.firstNode == null ) {
            this.firstNode = node;
            this.lastNode = node;;
        } else {
            this.lastNode.setNext( node );
            node.setPrevious( this.lastNode );
            this.lastNode = node;
        }
        this.size++;
    }

    /**
     * Removes a <code>LinkedListNode</code> from the list. This works by attach the previous reference to the child reference.
     * When the node to be removed is the first node it calls <code>removeFirst()</code>. When the node to be removed is the last node
     * it calls <code>removeLast()</code>.
     * 
     * @param node
     *      The <code>LinkedListNode</code> to be removed.
     */
    public void remove(final LinkedListNode node) {
        if ( (this.firstNode != node) && (this.lastNode != node) ) {
            node.getPrevious().setNext( node.getNext() );
            node.getNext().setPrevious( node.getPrevious() );
            this.size--;
            node.setPrevious( null );
            node.setNext( null );

        } else {
            if ( this.firstNode == node ) {
                removeFirst();
            } else if ( this.lastNode == node ) {
                removeLast();
            }
        }
    }

    /**
     * Return the first node in the list
     * @return
     *      The first <code>LinkedListNode</code>.
     */
    public final LinkedListNode getFirst() {
        return this.firstNode;
    }

    /**
     * Return the last node in the list
     * @return
     *      The last <code>LinkedListNode</code>.
     */
    public final LinkedListNode getLast() {
        return this.lastNode;
    }

    /**
     * Remove the first node from the list. The next node then becomes the first node. If this is the last 
     * node then both first and last node references are set to null.
     * 
     * @return
     *      The first <code>LinkedListNode</code>.
     */
    public LinkedListNode removeFirst() {
        if ( this.firstNode == null ) {
            return null;
        }
        final LinkedListNode node = this.firstNode;
        this.firstNode = node.getNext();
        node.setNext( null );
        if ( this.firstNode != null ) {
            this.firstNode.setPrevious( null );
        } else {
            this.lastNode = null;
        }
        this.size--;
        return node;
    }

    public void insertAfter(final LinkedListNode existingNode,
                            final LinkedListNode newNode) {
        if ( newNode.getPrevious() != null || newNode.getNext() != null ) {
            //do nothing if this node is already inserted somewhere
            return;
        }

        if ( existingNode == null ) {
            // if existing node is null, then insert it as a first node
            final LinkedListNode node = this.firstNode;
            node.setPrevious( newNode );
            newNode.setNext( node );
            this.firstNode = newNode;
        } else {
            existingNode.getNext().setPrevious( newNode );
            newNode.setNext( existingNode.getNext() );
            existingNode.setNext( newNode );
            newNode.setPrevious( existingNode );
        }
        this.size++;
    }

    /**
     * Remove the last node from the list. The previous node then becomes the last node. If this is the last 
     * node then both first and last node references are set to null.
     * 
     * @return
     *      The first <code>LinkedListNode</code>.
     */
    public LinkedListNode removeLast() {
        if ( this.lastNode == null ) {
            return null;
        }
        final LinkedListNode node = this.lastNode;
        this.lastNode = node.getPrevious();
        node.setPrevious( null );
        if ( this.lastNode != null ) {
            this.lastNode.setNext( null );
        } else {
            this.firstNode = this.lastNode;
        }
        this.size--;
        return node;
    }

    /**
     * @return
     *      boolean value indicating the empty status of the list
     */
    public final boolean isEmpty() {
        return (this.firstNode == null);
    }

    /**
     * Iterates the list removing all the nodes until there are no more nodes to remove. 
     */
    public void clear() {
        while ( removeFirst() != null ) {
        }
    }

    /**
     * @return
     *     return size of the list as an int
     */
    public final int size() {
        return this.size;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        for ( LinkedListNode node = this.firstNode; node != null; node = node.getNext() ) {
            result = PRIME * result + node.hashCode();
        }
        return result;
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != LinkedList.class ) {
            return false;
        }

        final LinkedList other = (LinkedList) object;

        if ( this.size() != other.size() ) {
            return false;
        }

        for ( LinkedListNode thisNode = this.firstNode, otherNode = other.firstNode; thisNode != null && otherNode != null; thisNode = thisNode.getNext(), otherNode = otherNode.getNext() ) {
            if ( !thisNode.equals( otherNode ) ) {
                return false;
            }
        }
        return true;
    }

    public Iterator iterator() {
        this.iterator.reset( this );
        return this.iterator();
    }
    
    /**
     * Returns a list iterator
     * @return
     */
    public class LinkedListIterator {
        private LinkedList list;
        private LinkedListNode current;
        
        public  void  reset( LinkedList list) {
            this.list = list;
        }
        
        public LinkedListNode next() {
            if (  this.current == null ) {
               this.current = this.list.firstNode;
            }
            LinkedListNode node  = this.current;
            current = current.getNext();
            return node;
        }
    }

}
