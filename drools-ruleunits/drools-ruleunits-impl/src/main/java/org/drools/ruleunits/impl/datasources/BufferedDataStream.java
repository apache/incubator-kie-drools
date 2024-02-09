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
package org.drools.ruleunits.impl.datasources;

import java.util.Iterator;

import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStream;

public class BufferedDataStream<T> extends AbstractDataSource<T> implements DataStream<T> {

    private final LimitedBufferedList<T> list;

    protected BufferedDataStream(int size) {
        this.list = new LimitedBufferedList<>(size);
    }

    @Override
    public void append(T value) {
        list.add(value);
        forEachSubscriber(s -> s.insert(value));
    }

    @Override
    public void subscribe(DataProcessor<T> subscriber) {
        super.subscribe(subscriber);
        list.forEach(subscriber::insert);
    }

    private static class LimitedBufferedList<T> implements Iterable<T> {
        private final T[] array;

        private int head = 0;

        LimitedBufferedList(int size) {
            this.array = (T[]) new Object[size];
        }

        void add(T value) {
            array[head++ % array.length] = value;
        }

        int size() {
            return Math.min(head, array.length);
        }

        @Override
        public Iterator<T> iterator() {
            return new BufferedIterator();
        }

        private class BufferedIterator implements Iterator<T> {

            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public T next() {
                return head < array.length ? array[cursor++] : array[(head + cursor++) % array.length];
            }
        }
    }
}
