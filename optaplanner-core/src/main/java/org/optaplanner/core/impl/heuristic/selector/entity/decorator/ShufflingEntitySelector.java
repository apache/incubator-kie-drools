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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class ShufflingEntitySelector extends AbstractCachingEntitySelector {

    public ShufflingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType) {
        super(childEntitySelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator() {
        Collections.shuffle(cachedEntityList, workingRandom);
        logger.trace("    Shuffled cachedEntityList with size ({}) in entitySelector({}).",
                cachedEntityList.size(), this);
        return cachedEntityList.iterator();
    }

    @Override
    public ListIterator<Object> listIterator() {
        Collections.shuffle(cachedEntityList, workingRandom);
        logger.trace("    Shuffled cachedEntityList with size ({}) in entitySelector({}).",
                cachedEntityList.size(), this);
        return cachedEntityList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // Presumes that listIterator() has already been called and shuffling would be bad
        return cachedEntityList.listIterator(index);
    }

    @Override
    public String toString() {
        return "Shuffling(" + childEntitySelector + ")";
    }

}
