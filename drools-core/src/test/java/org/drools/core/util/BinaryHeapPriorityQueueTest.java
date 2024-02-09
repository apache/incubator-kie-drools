/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.core.util.Queue.QueueEntry;
import org.junit.Test;

public class BinaryHeapPriorityQueueTest {
    @Test
    public void testOptimised() {
        final Random random = new Random();
        final List<QueueEntry> items = new ArrayList<>();

        final BinaryHeapQueue queue = new BinaryHeapQueue( NaturalComparator.INSTANCE,
                                                           100000 );

        for ( int i = 0; i < 100000; ++i ) {
            items.add( new LongQueueable( queue, random.nextLong() ) );
        }

        final long startEnqueue = System.currentTimeMillis();

        for ( final Iterator<QueueEntry> i = items.iterator(); i.hasNext(); ) {
            queue.enqueue(i.next() );
        }

        final long elapsedEnqueue = System.currentTimeMillis() - startEnqueue;

        final long startDequeue = System.currentTimeMillis();

        for ( final Iterator<QueueEntry> i = items.iterator(); i.hasNext(); ) {
            i.next().dequeue();
        }

        //        while (!queue.isEmpty()) {
        //            queue.dequeue();
        //        }

        final long elapsedDequeue = System.currentTimeMillis() - startDequeue;

        //        System.out.println( "elapsedEnqueue = " + elapsedEnqueue );
        //        System.out.println( "elapsedDequeue = " + elapsedDequeue );
    }

    @Test
    public void testBasic() {
        final Random random = new Random();
        final List items = new ArrayList();

        final BinaryHeapQueue<QueueEntry> queue = new BinaryHeapQueue<>( NaturalComparator.INSTANCE );

        for ( int i = 0; i < 100000; ++i ) {
            items.add( new LongQueueable( queue, random.nextLong() ) );
        }

        final long startEnqueue = System.currentTimeMillis();

        for ( final Iterator<QueueEntry> i = items.iterator(); i.hasNext(); ) {
            queue.enqueue(i.next() );
        }

        final long elapsedEnqueue = System.currentTimeMillis() - startEnqueue;

        final long startDequeue = System.currentTimeMillis();

        for ( final Iterator<QueueEntry> i = items.iterator(); i.hasNext(); ) {
            queue.enqueue(i.next() );
        }

        final long elapsedDequeue = System.currentTimeMillis() - startDequeue;
    }

    public class LongQueueable implements QueueEntry, Comparable {
        private final Long value;

        private BinaryHeapQueue queue;

        private int   index;

        public LongQueueable(BinaryHeapQueue queue,
                             final long value) {
            this.queue = queue;
            this.value = Long.valueOf( value );
        }

        public void setQueueIndex(final int index) {
            this.index = index;
        }

        public int getQueueIndex() {
            return this.index;
        }

        public void dequeue() {
            this.queue.dequeue( this.index );
        }

        public boolean isQueued() {
            return false;
        }

        public void setQueued(boolean activated) {
        }

        public int compareTo(final Object object) {
            return this.value.compareTo( ((LongQueueable) object).value);
        }

        public String toString() {
            return this.value.toString();
        }
    }
}
