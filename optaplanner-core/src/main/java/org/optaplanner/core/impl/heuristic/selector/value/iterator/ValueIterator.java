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

package org.optaplanner.core.impl.heuristic.selector.value.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for planning values.
 * @see Iterator
 */
public interface ValueIterator extends Iterator<Object> {

    /**
     * Follows the specification of {@link Iterator#hasNext()}.
     *
     * @param entity never null
     * @return true if there are more planning values
     */
    boolean hasNext(Object entity);

    /**
     * Follows the specification of {@link Iterator#next()}.
     *
     * @param entity never null
     * @return the next planning value
     * @exception NoSuchElementException if there are no more planning values
     */
    Object next(Object entity);

}
