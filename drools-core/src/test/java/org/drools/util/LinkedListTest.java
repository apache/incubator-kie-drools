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

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LinkedListTest {

    LinkedList     list  = null;
    LinkedListNode node1 = null;
    LinkedListNode node2 = null;
    LinkedListNode node3 = null;

    @Before
    public void setUp() throws Exception {
        this.list = new LinkedList();
        this.node1 = new AbstractBaseLinkedListNode();
        this.node2 = new AbstractBaseLinkedListNode();
        this.node3 = new AbstractBaseLinkedListNode();
    }

    /*
     * Test method for 'org.drools.util.LinkedList.add(LinkedListNode)'
     */
    @Test
    public void testAdd() {
        this.list.add( this.node1 );
        assertNull( "Node1 previous should be null",
                           this.node1.getPrevious() );
        assertNull( "Node1 next should be null",
                           this.node1.getNext() );
        assertSame( "First node should be node1",
                           this.list.getFirst(),
                           this.node1 );
        assertSame( "Last node should be node1",
                           this.list.getLast(),
                           this.node1 );

        this.list.add( this.node2 );
        assertSame( "node1 next should be node2",
                           this.node1.getNext(),
                           this.node2 );
        assertSame( "node2 previous should be node1",
                           this.node2.getPrevious(),
                           this.node1 );
        assertSame( "First node should be node1",
                           this.list.getFirst(),
                           this.node1 );
        assertSame( "Last node should be node2",
                           this.list.getLast(),
                           this.node2 );

        this.list.add( this.node3 );
        assertSame( "node2 next should be node3",
                           this.node2.getNext(),
                           this.node3 );
        assertSame( "node3 previous should be node2",
                           this.node3.getPrevious(),
                           this.node2 );
        assertEquals( "LinkedList should have 3 nodes",
                             this.list.size(),
                             3 );
        assertSame( "First node should be node1",
                           this.list.getFirst(),
                           this.node1 );
        assertSame( "Last node should be node3",
                           this.list.getLast(),
                           this.node3 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.remove(LinkedListNode)'
     */
    @Test
    public void testRemove() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame( "Node2 previous should be node1",
                           this.node2.getPrevious(),
                           this.node1 );
        assertSame( "Node2 next should be node3",
                           this.node2.getNext(),
                           this.node3 );
        this.list.remove( this.node2 );
        assertNull( "Node2 previous should be null",
                           this.node2.getPrevious() );
        assertNull( "Node2 next should be null",
                           this.node2.getNext() );

        assertNull( "Node1 previous should be null",
                           this.node1.getPrevious() );
        assertSame( "Node1 next should be node3",
                           this.node1.getNext(),
                           this.node3 );
        this.list.remove( this.node1 );
        assertNull( "Node1 previous should be null",
                           this.node1.getPrevious() );
        assertNull( "Node1 next should be null",
                           this.node1.getNext() );

        assertNull( "Node3 previous should be null",
                           this.node3.getPrevious() );
        assertNull( "Node3 next should be null",
                           this.node3.getNext() );
        this.list.remove( this.node3 );
        assertNull( "Node3 previous should be null",
                           this.node3.getPrevious() );
        assertNull( "Node3 next should be null",
                           this.node3.getNext() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.getFirst()'
     */
    @Test
    public void testGetFirst() {
        assertNull( "Empty list should return null on getFirst()",
                           this.list.getFirst() );
        this.list.add( this.node1 );
        assertSame( "List should return node1 on getFirst()",
                           this.list.getFirst(),
                           this.node1 );
        this.list.add( this.node2 );
        assertSame( "List should return node1 on getFirst()",
                           this.list.getFirst(),
                           this.node1 );
        this.list.add( this.node3 );
        assertSame( "List should return node1 on getFirst()",
                           this.list.getFirst(),
                           this.node1 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.getLast()'
     */
    @Test
    public void testGetLast() {
        assertNull( "Empty list should return null on getLast()",
                           this.list.getLast() );
        this.list.add( this.node1 );
        assertSame( "List should return node1 on getLast()",
                           this.list.getLast(),
                           this.node1 );
        this.list.add( this.node2 );
        assertSame( "List should return node2 on getLast()",
                           this.list.getLast(),
                           this.node2 );
        this.list.add( this.node3 );
        assertSame( "List should return node3 on getLast()",
                           this.list.getLast(),
                           this.node3 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.removeFirst()'
     */
    @Test
    public void testRemoveFirst() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame( "List should return node1 on getFirst()",
                           this.list.getFirst(),
                           this.node1 );
        this.list.removeFirst();
        assertSame( "List should return node2 on getFirst()",
                           this.list.getFirst(),
                           this.node2 );
        this.list.removeFirst();
        assertSame( "List should return node3 on getFirst()",
                           this.list.getFirst(),
                           this.node3 );
        this.list.removeFirst();
        assertNull( "Empty list should return null on getFirst()",
                           this.list.getFirst() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.removeLast()'
     */
    @Test
    public void testRemoveLast() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertSame( "List should return node1 on getLast()",
                           this.list.getLast(),
                           this.node3 );
        this.list.removeLast();
        assertSame( "List should return node2 on getLast()",
                           this.list.getLast(),
                           this.node2 );
        this.list.removeLast();
        assertSame( "List should return node3 on getLast()",
                           this.list.getLast(),
                           this.node1 );
        this.list.removeLast();
        assertNull( "Empty list should return null on getLast()",
                           this.list.getLast() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.isEmpty()'
     */
    @Test
    public void testIsEmpty() {
        assertTrue( "Empty list should return true on isEmpty()",
                           this.list.isEmpty() );
        this.list.add( this.node1 );
        assertFalse( "Not empty list should return false on isEmpty()",
                            this.list.isEmpty() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.clear()'
     */
    @Test
    public void testClear() {
        this.list.add( this.node1 );
        this.list.add( this.node2 );
        this.list.add( this.node3 );

        assertEquals( "List size should be 3",
                             this.list.size(),
                             3 );
        this.list.clear();
        assertEquals( "Empty list should have size 0",
                             this.list.size(),
                             0 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.size()'
     */
    @Test
    public void testSize() {
        this.list.add( this.node1 );
        assertEquals( "LinkedList should have 1 node",
                             this.list.size(),
                             1 );

        this.list.add( this.node2 );
        assertEquals( "LinkedList should have 2 nodes",
                             this.list.size(),
                             2 );

        this.list.add( this.node3 );
        assertEquals( "LinkedList should have 3 nodes",
                             this.list.size(),
                             3 );
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
