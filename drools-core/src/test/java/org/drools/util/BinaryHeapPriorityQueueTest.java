/**
 * Copyright 2010 JBoss Inc
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.Queue;
import org.drools.core.util.Queueable;

import junit.framework.TestCase;

public class BinaryHeapPriorityQueueTest extends TestCase {
    public void testOptimised() {
        final Random random = new Random();
        final List items = new LinkedList();

        final Queue queue = new BinaryHeapQueue( NaturalComparator.INSTANCE,
                                                 100000 );

        for ( int i = 0; i < 100000; ++i ) {
            items.add( new LongQueueable( queue, random.nextLong() ) );
        }

        final long startEnqueue = System.currentTimeMillis();

        for ( final Iterator i = items.iterator(); i.hasNext(); ) {
            queue.enqueue( (Queueable) i.next() );
        }

        final long elapsedEnqueue = System.currentTimeMillis() - startEnqueue;

        final long startDequeue = System.currentTimeMillis();

        for ( final Iterator i = items.iterator(); i.hasNext(); ) {
            ((Queueable) i.next()).dequeue();
        }

        //        while (!queue.isEmpty()) {
        //            queue.dequeue();
        //        }

        final long elapsedDequeue = System.currentTimeMillis() - startDequeue;

        //        System.out.println( "elapsedEnqueue = " + elapsedEnqueue );
        //        System.out.println( "elapsedDequeue = " + elapsedDequeue );
    }

    public void testBasic() {
        final Random random = new Random();
        final List items = new LinkedList();

        final BinaryHeapQueue queue = new BinaryHeapQueue( NaturalComparator.INSTANCE,
                                                           100000 );

        for ( int i = 0; i < 100000; ++i ) {
            items.add( new LongQueueable( queue, random.nextLong() ) );
        }

        final long startEnqueue = System.currentTimeMillis();

        for ( final Iterator i = items.iterator(); i.hasNext(); ) {
            queue.enqueue( (Queueable) i.next() );
        }

        final long elapsedEnqueue = System.currentTimeMillis() - startEnqueue;

        final long startDequeue = System.currentTimeMillis();

        for ( final Iterator i = items.iterator(); i.hasNext(); ) {
            queue.enqueue( (Queueable) i.next() );
        }

        //        while (!queue.isEmpty()) {
        //            queue.pop();
        //        }

        final long elapsedDequeue = System.currentTimeMillis() - startDequeue;

        //        System.out.println( "elapsedEnqueue = " + elapsedEnqueue );
        //        System.out.println( "elapsedDequeue = " + elapsedDequeue );
    }
}