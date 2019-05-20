/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LinkedListTest {

    LinkedList     list  = null;
    LinkedListNode node1 = null;
    LinkedListNode node2 = null;
    LinkedListNode node3 = null;

    @BeforeEach
    public void setUp() throws Exception {
        this.list = new LinkedList();
        this.node1 = new AbstractBaseLinkedListNodeMock();
        this.node2 = new AbstractBaseLinkedListNodeMock();
        this.node3 = new AbstractBaseLinkedListNodeMock();
    }

    private static class AbstractBaseLinkedListNodeMock
            extends AbstractBaseLinkedListNode<AbstractBaseLinkedListNodeMock> {

    }

    /*
     * Test method for 'org.kie.util.LinkedList.add(LinkedListNode)'
     */
    @Test
    public void testAdd() {
        this.list.add( this.node1 );
        assertNull(this.node1.getPrevious(), "Node1 previous should be null");
        assertNull(this.node1.getNext(), "Node1 next should be null");
        assertSame(this.list.getFirst(), this.node1, "First node should be node1");
        assertSame(this.list.getLast(), this.node1, "Last node should be node1");

        this.list.add( this.node2 );
        assertSame(this.node1.getNext(), this.node2, "node1 next should be node2");
        assertSame(this.node2.getPrevious(), this.node1, "node2 previous should be node1");
        assertSame(this.list.getFirst(), this.node1, "First node should be node1");
        assertSame(this.list.getLast(), this.node2, "Last node should be node2");

        this.list.add( this.node3 );
        assertSame(this.node2.getNext(), this.node3, "node2 next should be node3");
        assertSame(this.node3.getPrevious(), this.node2, "node3 previous should be node2");
        assertEquals(this.list.size(), (Object) 3, "LinkedList should have 3 nodes");
        assertSame(this.list.getFirst(), this.node1, "First node should be node1");
        assertSame(this.list.getLast(), this.node3, "Last node should be node3");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.remove(LinkedListNode)'
     */
    @Test
    public void testRemove() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame(this.node2.getPrevious(), this.node1, "Node2 previous should be node1");
        assertSame(this.node2.getNext(), this.node3, "Node2 next should be node3");
        this.list.remove( this.node2 );
        assertNull(this.node2.getPrevious(), "Node2 previous should be null");
        assertNull(this.node2.getNext(), "Node2 next should be null");

        assertNull(this.node1.getPrevious(), "Node1 previous should be null");
        assertSame(this.node1.getNext(), this.node3, "Node1 next should be node3");
        this.list.remove( this.node1 );
        assertNull(this.node1.getPrevious(), "Node1 previous should be null");
        assertNull(this.node1.getNext(), "Node1 next should be null");

        assertNull(this.node3.getPrevious(), "Node3 previous should be null");
        assertNull(this.node3.getNext(), "Node3 next should be null");
        this.list.remove( this.node3 );
        assertNull(this.node3.getPrevious(), "Node3 previous should be null");
        assertNull(this.node3.getNext(), "Node3 next should be null");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.getFirst()'
     */
    @Test
    public void testGetFirst() {
        assertNull(this.list.getFirst(), "Empty list should return null on getFirst()");
        this.list.add( this.node1 );
        assertSame(this.list.getFirst(), this.node1, "List should return node1 on getFirst()");
        this.list.add( this.node2 );
        assertSame(this.list.getFirst(), this.node1, "List should return node1 on getFirst()");
        this.list.add( this.node3 );
        assertSame(this.list.getFirst(), this.node1, "List should return node1 on getFirst()");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.getLast()'
     */
    @Test
    public void testGetLast() {
        assertNull(this.list.getLast(), "Empty list should return null on getLast()");
        this.list.add( this.node1 );
        assertSame(this.list.getLast(), this.node1, "List should return node1 on getLast()");
        this.list.add( this.node2 );
        assertSame(this.list.getLast(), this.node2, "List should return node2 on getLast()");
        this.list.add( this.node3 );
        assertSame(this.list.getLast(), this.node3, "List should return node3 on getLast()");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.removeFirst()'
     */
    @Test
    public void testRemoveFirst() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame(this.list.getFirst(), this.node1, "List should return node1 on getFirst()");
        this.list.removeFirst();
        assertSame(this.list.getFirst(), this.node2, "List should return node2 on getFirst()");
        this.list.removeFirst();
        assertSame(this.list.getFirst(), this.node3, "List should return node3 on getFirst()");
        this.list.removeFirst();
        assertNull(this.list.getFirst(), "Empty list should return null on getFirst()");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.removeLast()'
     */
    @Test
    public void testRemoveLast() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame(this.list.getLast(), this.node3, "List should return node1 on getLast()");
        this.list.removeLast();
        assertSame(this.list.getLast(), this.node2, "List should return node2 on getLast()");
        this.list.removeLast();
        assertSame(this.list.getLast(), this.node1, "List should return node3 on getLast()");
        this.list.removeLast();
        assertNull(this.list.getLast(), "Empty list should return null on getLast()");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.isEmpty()'
     */
    @Test
    public void testIsEmpty() {
        assertTrue(this.list.isEmpty(), "Empty list should return true on isEmpty()");
        this.list.add( this.node1 );
        assertFalse(this.list.isEmpty(), "Not empty list should return false on isEmpty()");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.clear()'
     */
    @Test
    public void testClear() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertEquals(this.list.size(), (Object) 3, "List size should be 3");
        this.list.clear();
        assertEquals(this.list.size(), (Object) 0, "Empty list should have size 0");
    }

    /*
     * Test method for 'org.kie.util.LinkedList.size()'
     */
    @Test
    public void testSize() {
        this.list.add( this.node1 );
        assertEquals(this.list.size(), (Object) 1, "LinkedList should have 1 node");

        this.list.add( this.node2 );
        assertEquals(this.list.size(), (Object) 2, "LinkedList should have 2 nodes");

        this.list.add( this.node3 );
        assertEquals(this.list.size(), (Object) 3, "LinkedList should have 3 nodes");
    }

    @Test
    public void testInsertAfter() {
        try {
            this.list.insertAfter( null,
                                   this.node1 );
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("Should NOT raise NPE!");
        }
    }

}
