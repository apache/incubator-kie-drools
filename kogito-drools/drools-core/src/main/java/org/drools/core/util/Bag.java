/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.Serializable;
import java.util.*;
import java.util.Iterator;

public class Bag<T> implements Collection<T>, Serializable {

    private Map<T, Counter> map = new HashMap<T, Counter>();

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    public int sizeFor(T t) {
        Counter i = map.get(t);
        return i != null ? i.get() : 0;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return map.containsKey( o );
    }

    @Override
    public Iterator<T> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T1> T1[] toArray( T1[] a ) {
        return map.keySet().toArray( a );
    }

    @Override
    public boolean add( T t ) {
        Counter i = map.get(t);
        if (i == null) {
            map.put( t, new Counter() );
        } else {
            i.increment();
        }
        size++;
        return true;
    }

    @Override
    public boolean remove( Object o ) {
        Counter i = map.get(o);
        if (i == null) {
            return false;
        }
        if (i.decrement()) {
            map.remove( o );
        }
        size--;
        return true;
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        return map.keySet().containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        for (T t : c) {
            add(t);
        }
        return true;
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        boolean result = false;
        for (Object o : c) {
            result = remove(o) || result;
        }
        return result;
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        map.clear();
        size = 0;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public static class Counter implements Serializable {
        private int count;

        public Counter() {
            this(1);
        }

        public Counter(int count) {
            this.count = count;
        }

        public void increment() {
            count++;
        }

        public boolean decrement() {
            return --count == 0;
        }

        public int get() {
            return count;
        }

        @Override
        public String toString() {
            return "" + count;
        }
    }
}
