/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.util.RBTree.Node;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RBTreeTest {

    @Test
    public void testFindNearestNode() {
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        tree.insert( 10, "" + 10 );
        tree.insert( 20, "" + 20 );
        tree.insert( 25, "" + 25 );
        tree.insert( 15, "" + 15 );
        tree.insert( 5, "" + 5 );

        assertEquals(5, (int)tree.findNearestNode(2, false, RBTree.Boundary.LOWER).key);
        assertEquals(null, tree.findNearestNode(2, false, RBTree.Boundary.UPPER));
        assertEquals(5, (int)tree.findNearestNode(2, true, RBTree.Boundary.LOWER).key);
        assertEquals(null, tree.findNearestNode(2, true, RBTree.Boundary.UPPER));

        assertEquals(10, (int)tree.findNearestNode(5, false, RBTree.Boundary.LOWER).key);
        assertEquals(null, tree.findNearestNode(5, false, RBTree.Boundary.UPPER));
        assertEquals(5, (int)tree.findNearestNode(5, true, RBTree.Boundary.LOWER).key);
        assertEquals(5, (int)tree.findNearestNode(5, true, RBTree.Boundary.UPPER).key);

        assertEquals(15, (int)tree.findNearestNode(12, false, RBTree.Boundary.LOWER).key);
        assertEquals(10, (int)tree.findNearestNode(12, false, RBTree.Boundary.UPPER).key);
        assertEquals(20, (int)tree.findNearestNode(15, false, RBTree.Boundary.LOWER).key);
        assertEquals(10, (int)tree.findNearestNode(15, false, RBTree.Boundary.UPPER).key);
        assertEquals(15, (int)tree.findNearestNode(15, true, RBTree.Boundary.UPPER).key);
        assertEquals(15, (int)tree.findNearestNode(15, true, RBTree.Boundary.LOWER).key);

        assertEquals(20, (int)tree.findNearestNode(25, false, RBTree.Boundary.UPPER).key);
        assertEquals(null, tree.findNearestNode(25, false, RBTree.Boundary.LOWER));
        assertEquals(25, (int)tree.findNearestNode(25, true, RBTree.Boundary.LOWER).key);
        assertEquals(25, (int)tree.findNearestNode(25, true, RBTree.Boundary.UPPER).key);

        assertEquals(25, (int)tree.findNearestNode(27, false, RBTree.Boundary.UPPER).key);
        assertEquals(null, tree.findNearestNode(27, false, RBTree.Boundary.LOWER));
        assertEquals(25, (int)tree.findNearestNode(27, true, RBTree.Boundary.UPPER).key);
        assertEquals(null, tree.findNearestNode(27, true, RBTree.Boundary.LOWER));
    }

    @Test
    public void testRange() {
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        tree.insert( 10, "" + 10 );
        tree.insert( 20, "" + 20 );
        tree.insert( 25, "" + 25 );
        tree.insert( 15, "" + 15 );
        tree.insert( 5, "" + 5 );

        FastIterator fastIterator = tree.range(2, true, 15, false);
        Node<Integer, String> node = (Node<Integer, String>)fastIterator.next(null);
        assertEquals(5, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertEquals(10, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertNull(node);

        fastIterator = tree.range(2, true, 5, false);
        node = (Node<Integer, String>)fastIterator.next(null);
        assertNull(node);

        fastIterator = tree.range(5, false, 35, false);
        node = (Node<Integer, String>)fastIterator.next(null);
        assertEquals(10, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertEquals(15, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertEquals(20, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertEquals(25, (int)node.key);
        node = (Node<Integer, String>)fastIterator.next(node);
        assertNull(node);
    }

    @Test
    public void testIterator() {
        final int ITEMS = 10000;
        RBTree<Integer, String> tree = new RBTree<Integer, String>();
        Random random = new Random(0);
        for (int i = 0; i < ITEMS; i++) {
            int key = random.nextInt();
            tree.insert( key, "" + key );
        }

        int i = 0;
        FastIterator fastIterator = tree.fastIterator();
        int lastKey = Integer.MIN_VALUE;
        for (Node<Integer, String> node = (Node<Integer, String>)fastIterator.next(null); node != null; node = (Node<Integer, String>)fastIterator.next(node)) {
            int currentKey = node.key;
            if (currentKey < lastKey) {
                fail(currentKey + " should be greater than " + lastKey);
            }
            lastKey = currentKey;
            i++;
        }

        assertEquals(ITEMS, i);
    }

    @Test @Ignore
    public void testLargeData() {
        int range = 6000000;
        for ( int i = 0; i < 10; i++ ) {
            // produces duplicate entry, isolated in test1
            long startTime = System.currentTimeMillis();
            generateAndTest( 90000, range-90000, range, 1 );
            long endTime = System.currentTimeMillis();

            System.out.println( endTime - startTime );
        }
    }

    @Test @Ignore
    public void testLargeData2() {
        int range = 6000000;
        for ( int i = 0; i < 10; i++ ) {
            // produces duplicate entry, isolated in test1
            long startTime = System.currentTimeMillis();
            generateAndTest2( 90000, range-90000, range, 1 );
            long endTime = System.currentTimeMillis();

            System.out.println( endTime - startTime );
        }
    }


    public void generateAndTest(int start,
                                int end,
                                int range,
                                int increment) {
        //System.out.println( "generate tree" );
        RBTree<Integer, String> tree = new RBTree<Integer, String>();

        for ( int i = 0; i <= range; i = i + increment ) {
            tree.insert( i, "" + i );
        }

        //System.out.println( "test data with tree" );
        checkResults( tree,
                range,
                start,
                end,
                increment );

        //tree.print();
    }

    public void generateAndTest2(int start,
                                 int end,
                                 int range,
                                 int increment) {
        //System.out.println( "generate tree" );
        //RBTree<Integer, String> tree = new RBTree<Integer, String>();
        TreeMap<Integer, String> tree = new TreeMap<Integer, String>();

        for ( int i = 0; i <= range; i = i + increment ) {
            tree.put( i, "" + i );
        }

        //System.out.println( "test data with tree" );
        checkResults2( tree,
                range,
                start,
                end,
                increment );

        //tree.print();
    }

    public void checkResults(RBTree<Integer, String> tree,
                             int range,
                             int start,
                             int end,
                             int increment) {
        FastIterator it = tree.range( start, true, end, true );
        Entry entry = null;
        int i = 0;
        List<Integer> actual = new ArrayList<Integer>();

        //System.out.println( start + ":" + end + ":" + (((end - start) / increment) + 1) );

        while ( (entry = it.next( entry )) != null ) {
            Node<Integer, String> node = (Node<Integer, String>) entry;
        }

        for ( i = 0; i < range; i = i + increment ) {
            tree.delete(i);
        }

    }

    public void checkResults2(TreeMap<Integer, String> tree,
                              int range,
                              int start,
                              int end,
                              int increment) {
        //FastIterator it = tree.range( start, true, end, true );
        SortedMap<Integer, String> map =  tree.subMap( start, end );


        int i = 0;
        List<Integer> actual = new ArrayList<Integer>();


        for (Iterator<java.util.Map.Entry<Integer, String>> it = map.entrySet().iterator(); it.hasNext(); ) {
            java.util.Map.Entry<Integer, String> entry = it.next();
        }

        for ( i = 0; i < range; i = i + increment ) {
            tree.remove( i );
        }

    }
}
