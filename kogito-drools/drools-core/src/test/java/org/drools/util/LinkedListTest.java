package org.drools.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class LinkedListTest extends TestCase {

    LinkedList     list  = null;
    LinkedListNode node1 = null;
    LinkedListNode node2 = null;
    LinkedListNode node3 = null;

    protected void setUp() throws Exception {
        super.setUp();
        list = new LinkedList();
        node1 = new AbstractBaseLinkedListNode();
        node2 = new AbstractBaseLinkedListNode();
        node3 = new AbstractBaseLinkedListNode();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.util.LinkedList.add(LinkedListNode)'
     */
    public void testAdd() {
        list.add( node1 );
        Assert.assertNull( "Node1 previous should be null",
                           node1.getPrevious() );
        Assert.assertNull( "Node1 next should be null",
                           node1.getNext() );
        Assert.assertSame( "First node should be node1",
                           list.getFirst(),
                           node1 );
        Assert.assertSame( "Last node should be node1",
                           list.getLast(),
                           node1 );

        list.add( node2 );
        Assert.assertSame( "node1 next should be node2",
                           node1.getNext(),
                           node2 );
        Assert.assertSame( "node2 previous should be node1",
                           node2.getPrevious(),
                           node1 );
        Assert.assertSame( "First node should be node1",
                           list.getFirst(),
                           node1 );
        Assert.assertSame( "Last node should be node2",
                           list.getLast(),
                           node2 );

        list.add( node3 );
        Assert.assertSame( "node2 next should be node3",
                           node2.getNext(),
                           node3 );
        Assert.assertSame( "node3 previous should be node2",
                           node3.getPrevious(),
                           node2 );
        Assert.assertEquals( "LinkedList should have 3 nodes",
                             list.size(),
                             3 );
        Assert.assertSame( "First node should be node1",
                           list.getFirst(),
                           node1 );
        Assert.assertSame( "Last node should be node3",
                           list.getLast(),
                           node3 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.remove(LinkedListNode)'
     */
    public void testRemove() {
        list.add( node1 );
        list.add( node2 );
        list.add( node3 );

        Assert.assertSame( "Node2 previous should be node1",
                           node2.getPrevious(),
                           node1 );
        Assert.assertSame( "Node2 next should be node3",
                           node2.getNext(),
                           node3 );
        list.remove( node2 );
        Assert.assertNull( "Node2 previous should be null",
                           node2.getPrevious() );
        Assert.assertNull( "Node2 next should be null",
                           node2.getNext() );

        Assert.assertNull( "Node1 previous should be null",
                           node1.getPrevious() );
        Assert.assertSame( "Node1 next should be node3",
                           node1.getNext(),
                           node3 );
        list.remove( node1 );
        Assert.assertNull( "Node1 previous should be null",
                           node1.getPrevious() );
        Assert.assertNull( "Node1 next should be null",
                           node1.getNext() );

        Assert.assertNull( "Node3 previous should be null",
                           node3.getPrevious() );
        Assert.assertNull( "Node3 next should be null",
                           node3.getNext() );
        list.remove( node3 );
        Assert.assertNull( "Node3 previous should be null",
                           node3.getPrevious() );
        Assert.assertNull( "Node3 next should be null",
                           node3.getNext() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.getFirst()'
     */
    public void testGetFirst() {
        Assert.assertNull( "Empty list should return null on getFirst()",
                           list.getFirst() );
        list.add( node1 );
        Assert.assertSame( "List should return node1 on getFirst()",
                           list.getFirst(),
                           node1 );
        list.add( node2 );
        Assert.assertSame( "List should return node1 on getFirst()",
                           list.getFirst(),
                           node1 );
        list.add( node3 );
        Assert.assertSame( "List should return node1 on getFirst()",
                           list.getFirst(),
                           node1 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.getLast()'
     */
    public void testGetLast() {
        Assert.assertNull( "Empty list should return null on getLast()",
                           list.getLast() );
        list.add( node1 );
        Assert.assertSame( "List should return node1 on getLast()",
                           list.getLast(),
                           node1 );
        list.add( node2 );
        Assert.assertSame( "List should return node2 on getLast()",
                           list.getLast(),
                           node2 );
        list.add( node3 );
        Assert.assertSame( "List should return node3 on getLast()",
                           list.getLast(),
                           node3 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.removeFirst()'
     */
    public void testRemoveFirst() {
        list.add( node1 );
        list.add( node2 );
        list.add( node3 );

        Assert.assertSame( "List should return node1 on getFirst()",
                           list.getFirst(),
                           node1 );
        list.removeFirst();
        Assert.assertSame( "List should return node2 on getFirst()",
                           list.getFirst(),
                           node2 );
        list.removeFirst();
        Assert.assertSame( "List should return node3 on getFirst()",
                           list.getFirst(),
                           node3 );
        list.removeFirst();
        Assert.assertNull( "Empty list should return null on getFirst()",
                           list.getFirst() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.removeLast()'
     */
    public void testRemoveLast() {
        list.add( node1 );
        list.add( node2 );
        list.add( node3 );

        Assert.assertSame( "List should return node1 on getLast()",
                           list.getLast(),
                           node3 );
        list.removeLast();
        Assert.assertSame( "List should return node2 on getLast()",
                           list.getLast(),
                           node2 );
        list.removeLast();
        Assert.assertSame( "List should return node3 on getLast()",
                           list.getLast(),
                           node1 );
        list.removeLast();
        Assert.assertNull( "Empty list should return null on getLast()",
                           list.getLast() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.isEmpty()'
     */
    public void testIsEmpty() {
        Assert.assertTrue( "Empty list should return true on isEmpty()",
                           list.isEmpty() );
        list.add( node1 );
        Assert.assertFalse( "Not empty list should return false on isEmpty()",
                            list.isEmpty() );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.clear()'
     */
    public void testClear() {
        list.add( node1 );
        list.add( node2 );
        list.add( node3 );

        Assert.assertEquals( "List size should be 3",
                             list.size(),
                             3 );
        list.clear();
        Assert.assertEquals( "Empty list should have size 0",
                             list.size(),
                             0 );
    }

    /*
     * Test method for 'org.drools.util.LinkedList.size()'
     */
    public void testSize() {
        list.add( node1 );
        Assert.assertEquals( "LinkedList should have 1 node",
                             list.size(),
                             1 );

        list.add( node2 );
        Assert.assertEquals( "LinkedList should have 2 nodes",
                             list.size(),
                             2 );

        list.add( node3 );
        Assert.assertEquals( "LinkedList should have 3 nodes",
                             list.size(),
                             3 );
    }

}
