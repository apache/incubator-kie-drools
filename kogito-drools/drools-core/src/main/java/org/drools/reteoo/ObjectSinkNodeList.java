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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
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
 */
public class ObjectSinkNodeList
    implements
    Externalizable {
    private static final long   serialVersionUID = 510l;

    private ObjectSinkNode firstNode;
    private ObjectSinkNode lastNode;

    private int                 size;

    /**
     * Construct an empty <code>LinkedList</code>
     */
    public ObjectSinkNodeList() {

    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        firstNode = (ObjectSinkNode) in.readObject();
        lastNode = (ObjectSinkNode) in.readObject();
        size = in.readInt();
        if ( firstNode == lastNode ) {
            // no other nodes
            return;
        }
        
        ObjectSinkNode current = firstNode;
        ObjectSinkNode previous = null;

        while ( current != lastNode) {
            ObjectSinkNode next = (ObjectSinkNode) in.readObject();
            current.setPreviousObjectSinkNode(previous);
            current.setNextObjectSinkNode(next);
            previous = current;
            current = next;
        }     
        
        // current equals last Node, so set previous (this avoids the null writting in stream
        current.setPreviousObjectSinkNode( previous );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( firstNode );
        out.writeObject( lastNode );
        out.writeInt( size );
        if ( firstNode == lastNode ) {
            // no other nodes
            return;
        }        
        for (ObjectSinkNode node = firstNode; node != null; node = node.getNextObjectSinkNode()) {
            out.writeObject(node.getNextObjectSinkNode());
        }        
    }

    /**
     * Add a <code>ObjectSinkNode</code> to the list. If the <code>LinkedList</code> is empty then the first and
     * last nodes are set to the added node.
     *
     * @param node
     *      The <code>ObjectSinkNode</code> to be added
     */
    public void add(final ObjectSinkNode node) {
        if ( this.firstNode == null ) {
            this.firstNode = node;
            this.lastNode = node;
        } else {
            this.lastNode.setNextObjectSinkNode( node );
            node.setPreviousObjectSinkNode( this.lastNode );
            this.lastNode = node;
        }
        this.size++;
    }

    /**
     * Removes a <code>ObjectSinkNode</code> from the list. This works by attach the previous reference to the child reference.
     * When the node to be removed is the first node it calls <code>removeFirst()</code>. When the node to be removed is the last node
     * it calls <code>removeLast()</code>.
     *
     * @param node
     *      The <code>ObjectSinkNode</code> to be removed.
     */
    public void remove(final ObjectSinkNode node) {
        if ( (this.firstNode != node) && (this.lastNode != node) ) {
            node.getPreviousObjectSinkNode().setNextObjectSinkNode( node.getNextObjectSinkNode() );
            node.getNextObjectSinkNode().setPreviousObjectSinkNode( node.getPreviousObjectSinkNode() );
            this.size--;
            node.setPreviousObjectSinkNode( null );
            node.setNextObjectSinkNode( null );

        } else {
            if ( this.firstNode == node ) {
                removeFirst();
            } else {
                removeLast();
            }
        }
    }

    /**
     * Return the first node in the list
     * @return
     *      The first <code>ObjectSinkNode</code>.
     */
    public final ObjectSinkNode getFirst() {
        return this.firstNode;
    }

    /**
     * Return the last node in the list
     * @return
     *      The last <code>ObjectSinkNode</code>.
     */
    public final ObjectSinkNode getLast() {
        return this.lastNode;
    }

    /**
     * Remove the first node from the list. The next node then becomes the first node. If this is the last
     * node then both first and last node references are set to null.
     *
     * @return
     *      The first <code>ObjectSinkNode</code>.
     */
    public ObjectSinkNode removeFirst() {
        if ( this.firstNode == null ) {
            return null;
        }
        final ObjectSinkNode node = this.firstNode;
        this.firstNode = node.getNextObjectSinkNode();
        node.setNextObjectSinkNode( null );
        if ( this.firstNode != null ) {
            this.firstNode.setPreviousObjectSinkNode( null );
        } else {
            this.lastNode = null;
        }
        this.size--;
        return node;
    }

    /**
     * Remove the last node from the list. The previous node then becomes the last node. If this is the last
     * node then both first and last node references are set to null.
     *
     * @return
     *      The first <code>ObjectSinkNode</code>.
     */
    public ObjectSinkNode removeLast() {
        if ( this.lastNode == null ) {
            return null;
        }
        final ObjectSinkNode node = this.lastNode;
        this.lastNode = node.getPreviousObjectSinkNode();
        node.setPreviousObjectSinkNode( null );
        if ( this.lastNode != null ) {
            this.lastNode.setNextObjectSinkNode( null );
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

    /**
     * Returns a list iterator
     * @return
     */
    public Iterator iterator() {
        return new Iterator() {
            private ObjectSinkNode currentNode = null;
            private ObjectSinkNode nextNode    = getFirst();

            public boolean hasNext() {
                return (this.nextNode != null);
            }

            public Object next() {
                this.currentNode = this.nextNode;
                if ( this.currentNode != null ) {
                    this.nextNode = this.currentNode.getNextObjectSinkNode();
                } else {
                    throw new NoSuchElementException( "No more elements to return" );
                }
                return this.currentNode;
            }

            public void remove() {
                if ( this.currentNode != null ) {
                    ObjectSinkNodeList.this.remove( this.currentNode );
                    this.currentNode = null;
                } else {
                    throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                }
            }
        };
    }

}
