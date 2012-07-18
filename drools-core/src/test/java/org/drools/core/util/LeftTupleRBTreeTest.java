package org.drools.core.util;

import org.junit.Test;

import java.util.Random;

import org.drools.core.util.LeftTupleRBTree.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LeftTupleRBTreeTest {

    @Test
    public void testIterator() {
        final int ITEMS = 10000;
        LeftTupleRBTree<Integer> tree = new LeftTupleRBTree<Integer>();
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
}
