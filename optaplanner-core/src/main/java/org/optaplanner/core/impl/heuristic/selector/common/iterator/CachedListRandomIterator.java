/*
 * Copyright 2012 JBoss Inc
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
import java.util.Random;

/**
 * This {@link Iterator} does not shuffle and is never ending.
 * @param <S>
 */
public class CachedListRandomIterator<S> implements Iterator<S>  {

    protected final List<S> cachedList;
    protected final Random workingRandom;
    protected final boolean notEmpty;

    public CachedListRandomIterator(List<S> cachedList, Random workingRandom) {
        this.cachedList = cachedList;
        this.workingRandom = workingRandom;
        notEmpty = !cachedList.isEmpty();
    }

    public boolean hasNext() {
        return notEmpty;
    }

    public S next() {
        int index = workingRandom.nextInt(cachedList.size());
        return cachedList.get(index);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported.");
    }

}
