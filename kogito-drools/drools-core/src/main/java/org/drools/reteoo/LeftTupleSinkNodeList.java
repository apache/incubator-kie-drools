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
public class LeftTupleSinkNodeList
    implements
    Externalizable {
    private static final long serialVersionUID = 510l;

    private LeftTupleSinkNode firstNode;
    private LeftTupleSinkNode lastNode;

    private int               size;

    /**
     * Construct an empty <code>LinkedList</code>
     */
    public LeftTupleSinkNodeList() {
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        firstNode = (LeftTupleSinkNode) in.readObject();
        lastNode = (LeftTupleSinkNode) in.readObject();
        size = in.readInt();
        if ( firstNode == lastNode ) {
            // no other nodes
            return;
        }
        
        LeftTupleSinkNode current = firstNode;
        LeftTupleSinkNode previous = null;

        while ( current != lastNode) {
            LeftTupleSinkNode next = (LeftTupleSinkNode) in.readObject();
            current.setPreviousLeftTupleSinkNode(previous);
            current.setNextLeftTupleSinkNode(next);
            previous = current;
            current = next;
        }     
        
        // current equals last Node, so set previous (this avoids the null writting in stream
        current.setPreviousLeftTupleSinkNode( previous );        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( firstNode );
        out.writeObject( lastNode );
        out.writeInt( size );
        if ( firstNode == lastNode ) {
            // no other nodes
            return;
        }        
        for (LeftTupleSinkNode node = firstNode; node != null; node = node.getNextLeftTupleSinkNode()) {
            out.writeObject(node.getNextLeftTupleSinkNode());
        }        
    }

    /**
     * Add a <code>TupleSinkNode</code> to the list. If the <code>LinkedList</code> is empty then the first and
     * last nodes are set to the added node.
     *
     * @param node
     *      The <code>TupleSinkNode</code> to be added
     */
    public void add(final LeftTupleSinkNode node) {
        if ( this.firstNode == null ) {
            this.firstNode = node;
            this.lastNode = node;;
        } else {
            this.lastNode.setNextLeftTupleSinkNode( node );
            node.setPreviousLeftTupleSinkNode( this.lastNode );
            this.lastNode = node;
        }
        this.size++;
    }  

    /**
     * Removes a <code>TupleSinkNode</code> from the list. This works by attach the previous reference to the child reference.
     * When the node to be removed is the first node it calls <code>removeFirst()</code>. When the node to be removed is the last node
     * it calls <code>removeLast()</code>.
     *
     * @param node
     *      The <code>TupleSinkNode</code> to be removed.
     */
    public void remove(final LeftTupleSinkNode node) {
        if ( (this.firstNode != node) && (this.lastNode != node) ) {
            node.getPreviousLeftTupleSinkNode().setNextLeftTupleSinkNode( node.getNextLeftTupleSinkNode() );
            node.getNextLeftTupleSinkNode().setPreviousLeftTupleSinkNode( node.getPreviousLeftTupleSinkNode() );
            this.size--;
            node.setPreviousLeftTupleSinkNode( null );
            node.setNextLeftTupleSinkNode( null );

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
     *      The first <code>TupleSinkNode</code>.
     */
    public final LeftTupleSinkNode getFirst() {
        return this.firstNode;
    }

    /**
     * Return the last node in the list
     * @return
     *      The last <code>TupleSinkNode</code>.
     */
    public final LeftTupleSinkNode getLast() {
        return this.lastNode;
    }

    /**
     * Remove the first node from the list. The next node then becomes the first node. If this is the last
     * node then both first and last node references are set to null.
     *
     * @return
     *      The first <code>TupleSinkNode</code>.
     */
    public LeftTupleSinkNode removeFirst() {
        if ( this.firstNode == null ) {
            return null;
        }
        final LeftTupleSinkNode node = this.firstNode;
        this.firstNode = node.getNextLeftTupleSinkNode();
        node.setNextLeftTupleSinkNode( null );
        if ( this.firstNode != null ) {
            this.firstNode.setPreviousLeftTupleSinkNode( null );
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
     *      The first <code>TupleSinkNode</code>.
     */
    public LeftTupleSinkNode removeLast() {
        if ( this.lastNode == null ) {
            return null;
        }
        final LeftTupleSinkNode node = this.lastNode;
        this.lastNode = node.getPreviousLeftTupleSinkNode();
        node.setPreviousLeftTupleSinkNode( null );
        if ( this.lastNode != null ) {
            this.lastNode.setNextLeftTupleSinkNode( null );
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
            private LeftTupleSinkNode currentNode = null;
            private LeftTupleSinkNode nextNode    = getFirst();

            public boolean hasNext() {
                return (this.nextNode != null);
            }

            public Object next() {
                this.currentNode = this.nextNode;
                if ( this.currentNode != null ) {
                    this.nextNode = this.currentNode.getNextLeftTupleSinkNode();
                } else {
                    throw new NoSuchElementException( "No more elements to return" );
                }
                return this.currentNode;
            }

            public void remove() {
                if ( this.currentNode != null ) {
                    LeftTupleSinkNodeList.this.remove( this.currentNode );
                    this.currentNode = null;
                } else {
                    throw new IllegalStateException( "No item to remove. Call next() before calling remove()." );
                }
            }
        };
    }

}
