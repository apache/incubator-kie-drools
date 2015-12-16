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

package org.drools.reteoo.common;

import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.AgendaItem;
import org.drools.core.conflict.SequentialConflictResolver;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.Activation;
import org.drools.core.util.BinaryHeapQueue;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class ReteAgendaGroupQueueImpl extends AgendaGroupQueueImpl {

    public ReteAgendaGroupQueueImpl( String name, InternalKnowledgeBase kBase ) {
        super( name, kBase );
    }

    @Override
    protected BinaryHeapQueue initPriorityQueue( InternalKnowledgeBase kBase ) {
        return isSequential() ?
                new SynchronizedBinaryHeapQueue(new SequentialConflictResolver()) :
                new SynchronizedBinaryHeapQueue(kBase.getConfiguration().getConflictResolver());
    }

    @Override
    public Activation[] getActivations() {
        synchronized (this.priorityQueue) {
            return (Activation[]) this.priorityQueue.toArray(new AgendaItem[this.priorityQueue.size()]);
        }
    }

    public static class SynchronizedBinaryHeapQueue extends BinaryHeapQueue {

        public SynchronizedBinaryHeapQueue() {
            super();
        }

        public SynchronizedBinaryHeapQueue( Comparator comparator ) {
            super( comparator );
        }

        public SynchronizedBinaryHeapQueue( Comparator comparator, int capacity ) {
            super( comparator, capacity );
        }

        @Override
        public synchronized void clear() {
            super.clear();
        }

        @Override
        public synchronized Activation[] getAndClear() {
            return super.getAndClear();
        }

        @Override
        public synchronized boolean isEmpty() {
            return super.isEmpty();
        }

        @Override
        public synchronized boolean isFull() {
            return super.isFull();
        }

        @Override
        public synchronized int size() {
            return super.size();
        }

        @Override
        public synchronized Activation peek() {
            return super.peek();
        }

        @Override
        public synchronized void enqueue( Activation element ) {
            super.enqueue( element );
        }

        @Override
        public synchronized Activation dequeue() throws NoSuchElementException {
            return super.dequeue();
        }

        @Override
        public synchronized Activation dequeue( Activation activation ) {
            return super.dequeue( activation );
        }

        @Override
        public synchronized Object[] toArray( Object[] a ) {
            return super.toArray( a );
        }
    }
}
