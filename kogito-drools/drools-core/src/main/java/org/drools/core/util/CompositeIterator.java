/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.NoSuchElementException;

public class CompositeIterator<T> implements java.util.Iterator<T> {

    private final java.util.Iterator<T>[] iterators;
    private int counter = 0;
    private T currentNext;

    public CompositeIterator( java.util.Iterator<T>... iterators ) {
        this.iterators = iterators;
        this.currentNext = internalNext();
    }

    @Override
    public boolean hasNext() {
        return currentNext != null;
    }

    @Override
    public T next() {
        if (currentNext == null) {
            throw new NoSuchElementException();
        }
        T result = currentNext;
        currentNext = internalNext();
        return result;
    }

    private T internalNext() {
        while (counter < iterators.length) {
            if (iterators[counter].hasNext()) {
                return iterators[counter].next();
            } else {
                counter++;
            }
        }
        return null;
    }
}
