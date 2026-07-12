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

package org.optaplanner.constraint.streams.bavet.common.index;

/**
 * Index properties are cached in tuples and each tuple carries its unique instance.
 * <p>
 * Index properties are shallow immutable and implement {@link Object#equals(Object)} and {@link Object#hashCode()}.
 */
public interface IndexProperties {

    /**
     * Retrieves index property at a given position.
     *
     * @param index
     * @return never null
     * @param <Type_> {@link ComparisonIndexer} will expect this to implement {@link Comparable}.
     */
    <Type_> Type_ toKey(int index);

    /**
     * Retrieves an object to serve as a key in an index.
     * Instances retrieved using the same arguments must be {@link Object#equals(Object) equal}.
     *
     * @param <Type_> expected type of the key
     * @param from index of the first property to use, inclusive
     * @param to index of the last property to use, exclusive
     * @return never null
     */
    <Type_> Type_ toKey(int from, int to);

}
