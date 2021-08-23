/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;

import org.drools.core.spi.Activation;

public class ArrayQueue implements Queue, Externalizable {

    private java.util.Queue<Activation> queue = new ArrayDeque<>();

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( queue );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        this.queue = (java.util.Queue<Activation>) in.readObject();
    }

    @Override
    public void enqueue( Activation activation ) {
        queue.add( activation );
    }

    @Override
    public Activation dequeue() {
        return queue.poll();
    }

    @Override
    public void dequeue( Activation activation ) {
        queue.remove( activation );
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Activation[] getAndClear() {
        Activation[] activations = (Activation[]) toArray( new Activation[size()] );
        clear();
        return activations;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public Activation peek() {
        return queue.peek();
    }

    @Override
    public Object[] toArray( Object[] a ) {
        return queue.toArray(a);
    }
}
