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

package org.optaplanner.core.impl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CollectionUtils {

    /**
     * Creates a copy of the list, optionally in reverse order.
     *
     * @param originalList the list to copy, preferably {@link ArrayList}
     * @param reverse true if the resulting list should have its order reversed
     * @return mutable list, never null
     * @param <E> the type of elements in the list
     */
    public static <E> List<E> copy(List<E> originalList, boolean reverse) {
        if (!reverse) {
            return new ArrayList<>(originalList);
        }
        /*
         * Some move implementations on the hot path rely heavily on list reversal.
         * As such, the following implementation was benchmarked to perform as well as possible for lists of all sizes.
         * See PLANNER-2808 for details.
         */
        switch (originalList.size()) {
            case 0:
                return new ArrayList<>(0);
            case 1:
                List<E> singletonList = new ArrayList<>(1);
                singletonList.add(originalList.get(0));
                return singletonList;
            case 2:
                List<E> smallList = new ArrayList<>(2);
                smallList.add(originalList.get(1));
                smallList.add(originalList.get(0));
                return smallList;
            default:
                List<E> largeList = new ArrayList<>(originalList);
                Collections.reverse(largeList);
                return largeList;
        }
    }

    public static <T> List<T> concat(List<T> left, List<T> right) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        result.addAll(left);
        result.addAll(right);
        return result;
    }

    private CollectionUtils() {
        // No external instances.
    }

}
