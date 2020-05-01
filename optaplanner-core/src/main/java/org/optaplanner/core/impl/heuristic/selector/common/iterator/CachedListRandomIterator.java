/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * This {@link Iterator} does not shuffle and is never ending.
 *
 * @param <S> Selection type, for example a {@link Move} class, an entity class or a value class.
 */
public class CachedListRandomIterator<S> extends SelectionIterator<S> {

    protected final List<S> cachedList;
    protected final Random workingRandom;
    protected final boolean empty;

    public CachedListRandomIterator(List<S> cachedList, Random workingRandom) {
        this.cachedList = cachedList;
        this.workingRandom = workingRandom;
        empty = cachedList.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return !empty;
    }

    @Override
    public S next() {
        if (empty) {
            throw new NoSuchElementException();
        }
        int index = workingRandom.nextInt(cachedList.size());
        return cachedList.get(index);
    }

}
