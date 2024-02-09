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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.drools.core.util.Queue.QueueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

public class TreeSetQueue<T extends QueueEntry> implements Queue<T>, Externalizable {

    protected static final Logger log = LoggerFactory.getLogger(BinaryHeapQueue.class);

    /** The elements in this heap. */
    private TreeSet<T> elements;

    public TreeSetQueue() {

    }

    /**
     * Constructs a new <code>BinaryHeap</code>.
     *
     * @param comparator the comparator used to order the elements, null
     *                   means use natural order
     */
    public TreeSetQueue(final Comparator<T> comparator) {
        this.elements = new TreeSet<>(comparator);
    }

    //-----------------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        elements = (TreeSet<T>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(elements);
    }

    /**
     * Clears all elements from queue.
     */
    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public Collection<T> getAll() {
        return elements;
    }

    /**
     * Tests if queue is empty.
     *
     * @return <code>true</code> if queue is empty; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Returns the number of elements in this heap.
     *
     * @return the number of elements in this heap
     */
    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public T peek() {
        return isEmpty() ? null : elements.last();
    }

    /**
     * Inserts an Queueable into queue.
     *
     * @param element the Queueable to be inserted
     */
    @Override
    public void enqueue(final T element) {
        elements.add( element );
        element.setQueued(true);

        if ( log.isTraceEnabled() ) {
            log.trace( "Queue Added {}", element);
        }
    }

    /**
     * Returns the Queueable on top of heap and remove it.
     *
     * @return the Queueable at top of heap
     */
    @Override
    public T dequeue() {
        return elements.pollLast();
    }

    @Override
    public void dequeue(T activation) {
        elements.remove(activation);
    }

    @Override
    public String toString() {
        return Stream.of( elements ).filter(Objects::nonNull).collect(toList() ).toString();
    }
}