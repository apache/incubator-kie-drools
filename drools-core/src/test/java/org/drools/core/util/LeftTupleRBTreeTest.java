/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.util.TupleRBTree.Node;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class LeftTupleRBTreeTest {

    @Test
    public void testIterator() {
        final int ITEMS = 10000;
        TupleRBTree<Integer> tree = new TupleRBTree<Integer>();
        Random random = new Random(0);
        for (int i = 0; i < ITEMS; i++) {
            int key = random.nextInt();
            tree.insert( key );
        }

        int i = 0;
        FastIterator fastIterator = tree.fastIterator();
        int lastKey = Integer.MIN_VALUE;
        for (Node<Integer> node = (Node<Integer>)fastIterator.next(null); node != null; node = (Node<Integer>)fastIterator.next(node)) {
            int currentKey = node.key;
            if (currentKey < lastKey) {
                fail(currentKey + " should be greater than " + lastKey);
            }
            lastKey = currentKey;
            i++;
        }

        assertEquals(ITEMS, i);
    }

    @Test
    public void testRange() {
        // TupleRBTree.range() is not actually used by TupleIndexRBTree but fixing it to avoid future trouble
        TupleRBTree<Integer> tree = new TupleRBTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(25);
        tree.insert(15);
        tree.insert(5);

        FastIterator fastIterator = tree.range(2, true, 15, false);
        Node<Integer> node = (Node<Integer>) fastIterator.next(null);
        assertEquals(5, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertEquals(10, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertNull(node);

        fastIterator = tree.range(2, true, 5, false);
        node = (Node<Integer>) fastIterator.next(null);
        assertNull(node);

        fastIterator = tree.range(25, false, 35, true);
        node = (Node<Integer>) fastIterator.next(null);
        assertNull(node);

        fastIterator = tree.range(6, false, 9, false);
        node = (Node<Integer>) fastIterator.next(null);
        assertNull(node);

        fastIterator = tree.range(5, false, 35, false);
        node = (Node<Integer>) fastIterator.next(null);
        assertEquals(10, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertEquals(15, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertEquals(20, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertEquals(25, (int) node.key);
        node = (Node<Integer>) fastIterator.next(node);
        assertNull(node);

    }
}
