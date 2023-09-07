/*
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

package org.optaplanner.constraint.streams.bavet.common.collection;

/**
 * An entry of {@link TupleList}
 *
 * @param <T> The element type. Often a tuple.
 */
public final class TupleListEntry<T> {

    private TupleList<T> list;
    private final T element;
    TupleListEntry<T> previous;
    TupleListEntry<T> next;

    TupleListEntry(TupleList<T> list, T element, TupleListEntry<T> previous) {
        this.list = list;
        this.element = element;
        this.previous = previous;
        this.next = null;
    }

    public TupleListEntry<T> next() {
        return next;
    }

    public TupleListEntry<T> removeAndNext() {
        TupleListEntry<T> next = this.next;
        remove(); // Sets this.next = null
        return next;
    }

    public void remove() {
        if (list == null) {
            throw new IllegalStateException("The element (" + element + ") was already removed.");
        }
        list.remove(this);
        list = null;
    }

    public T getElement() {
        return element;
    }

    public TupleList<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        return element.toString();
    }

}
