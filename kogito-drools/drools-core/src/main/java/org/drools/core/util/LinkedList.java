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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.NoSuchElementException;

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
    Externalizable {
    private static final long  serialVersionUID = 510l;

    private LinkedListNode     firstNode;
    private LinkedListNode     lastNode;

    private int                size;

    private LinkedListIterator iterator;
    
    public static FastIterator fastIterator = new LinkedListFastIterator(); // contains no state, so ok to be static    

    /**
     * Construct an empty <code>LinkedList</code>
     */
    public LinkedList() {
        this.iterator = new LinkedListIterator();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        firstNode   = (LinkedListNode)in.readObject();
        lastNode    = (LinkedListNode)in.readObject();
        size        = in.readInt();
        iterator    = (LinkedListIterator)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(firstNode);
        out.writeObject(lastNode);
        out.writeInt(size);
        out.writeObject(iterator);
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
            this.lastNode = node;
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
        if ( this.firstNode == node ) {
            removeFirst();
        } else if ( this.lastNode == node ) {
            removeLast();
        } else {
            node.getPrevious().setNext( node.getNext() );
            (node.getNext()).setPrevious( node.getPrevious() );
            this.size--;
            node.setPrevious( null );
            node.setNext( null );
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
            if ( this.isEmpty() ) {
                this.firstNode = newNode;
                this.lastNode = newNode;
            } else {
                // if existing node is null, then insert it as a first node
                final LinkedListNode node = this.firstNode;
                node.setPrevious( newNode );
                newNode.setNext( node );
                this.firstNode = newNode;
            }
        } else if ( existingNode == this.lastNode ) {
            existingNode.setNext( newNode );
            newNode.setPrevious( existingNode );
            this.lastNode = newNode;
        } else {
            (existingNode.getNext()).setPrevious( newNode );
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

        if ( object == null || !(object instanceof LinkedList) ) {
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
        return this.iterator;
    }
    
    public FastIterator fastIterator() {
    	return fastIterator;
    }
    
    public static class LinkedListFastIterator implements FastIterator {
		public Entry next(Entry object) {
			return object.getNext();
		}
    }

    public java.util.Iterator javaUtilIterator() {
        return new JavaUtilIterator( this );
    }

    /**
     * Returns a list iterator
     * @return
     */
    public static class LinkedListIterator
        implements
        Iterator,
        Externalizable {
        private LinkedList     list;
        private LinkedListNode current;

        public void reset(final LinkedList list) {
            this.list = list;
            this.current = this.list.firstNode;
        }

        public Object next() {
            if ( this.current == null ) {
                return null;
            }
            final LinkedListNode node = this.current;
            this.current = this.current.getNext();
            return node;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            list    = (LinkedList)in.readObject();
            current = (LinkedListNode)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(list);
            out.writeObject(current);
        }

    }

    public static class JavaUtilIterator
        implements
        java.util.Iterator,
        Externalizable {
        private LinkedList     list;
        private LinkedListNode currentNode;
        private LinkedListNode nextNode;
        private boolean        immutable;

        public JavaUtilIterator() {

        }

        public JavaUtilIterator(final LinkedList list) {
            this( list,
                  true );
        }

        public JavaUtilIterator(final LinkedList list,
                                final boolean immutable) {
            this.list = list;
            this.nextNode = this.list.getFirst();
            this.immutable = immutable;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            list    = (LinkedList)in.readObject();
            currentNode = (LinkedListNode)in.readObject();
            nextNode = (LinkedListNode)in.readObject();
            immutable   = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(list);
            out.writeObject(currentNode);
            out.writeObject(nextNode);
            out.writeBoolean(immutable);
        }

        public boolean hasNext() {
            return (this.nextNode != null);
        }

        public Object next() {
            this.currentNode = this.nextNode;
            if ( this.currentNode != null ) {
                this.nextNode = this.currentNode.getNext();
            } else {
                throw new NoSuchElementException( "No more elements to return" );
            }
            return this.currentNode;
        }

        public void remove() {
            if ( this.immutable ) {
                throw new UnsupportedOperationException( "This  Iterator is immutable, you cannot call remove()" );
            }

            if ( this.currentNode != null ) {
                this.list.remove( this.currentNode );
                this.currentNode = null;
            } else {
                throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
            }
        }
    }

}
