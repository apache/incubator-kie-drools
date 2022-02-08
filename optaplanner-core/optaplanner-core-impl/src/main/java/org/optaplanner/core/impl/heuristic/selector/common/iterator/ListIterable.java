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

import java.util.List;
import java.util.ListIterator;

/**
 * An extension on the {@link Iterable} interface that supports {@link #listIterator()} and {@link #listIterator(int)}.
 *
 * @param <T> the element type
 */
public interface ListIterable<T> extends Iterable<T> {

    /**
     * @see List#listIterator()
     * @return never null, see {@link List#listIterator()}.
     */
    ListIterator<T> listIterator();

    /**
     * @see List#listIterator()
     * @param index lower than the size of this {@link ListIterable}, see {@link List#listIterator(int)}.
     * @return never null, see {@link List#listIterator(int)}.
     */
    ListIterator<T> listIterator(int index);

}
