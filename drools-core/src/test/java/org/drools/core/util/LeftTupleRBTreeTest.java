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
