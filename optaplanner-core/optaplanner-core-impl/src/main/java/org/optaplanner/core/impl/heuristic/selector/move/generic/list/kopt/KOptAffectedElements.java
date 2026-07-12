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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.List;

import org.optaplanner.core.impl.util.CollectionUtils;
import org.optaplanner.core.impl.util.Pair;

final class KOptAffectedElements {
    private final int wrappedStartIndex;
    private final int wrappedEndIndex;
    private final List<Pair<Integer, Integer>> affectedMiddleRangeList;

    private KOptAffectedElements(int wrappedStartIndex, int wrappedEndIndex,
            List<Pair<Integer, Integer>> affectedMiddleRangeList) {
        this.wrappedStartIndex = wrappedStartIndex;
        this.wrappedEndIndex = wrappedEndIndex;
        this.affectedMiddleRangeList = affectedMiddleRangeList;
    }

    static KOptAffectedElements forMiddleRange(int startInclusive, int endExclusive) {
        return new KOptAffectedElements(-1, -1, List.of(Pair.of(startInclusive, endExclusive)));
    }

    static KOptAffectedElements forWrappedRange(int startInclusive, int endExclusive) {
        return new KOptAffectedElements(startInclusive, endExclusive, List.of());
    }

    // ***********************************************
    // Simple getters
    // ***********************************************

    public int getWrappedStartIndex() {
        return wrappedStartIndex;
    }

    public int getWrappedEndIndex() {
        return wrappedEndIndex;
    }

    public List<Pair<Integer, Integer>> getAffectedMiddleRangeList() {
        return affectedMiddleRangeList;
    }

    // ***********************************************
    // Complex methods
    // ***********************************************
    public KOptAffectedElements merge(KOptAffectedElements other) {
        int newWrappedStartIndex = this.wrappedStartIndex;
        int newWrappedEndIndex = this.wrappedEndIndex;

        if (other.wrappedStartIndex != -1) {
            if (newWrappedStartIndex != -1) {
                newWrappedStartIndex = Math.min(other.wrappedStartIndex, newWrappedStartIndex);
                newWrappedEndIndex = Math.max(other.wrappedEndIndex, newWrappedEndIndex);
            } else {
                newWrappedStartIndex = other.wrappedStartIndex;
                newWrappedEndIndex = other.wrappedEndIndex;
            }
        }

        List<Pair<Integer, Integer>> newAffectedMiddleRangeList =
                CollectionUtils.concat(affectedMiddleRangeList, other.affectedMiddleRangeList);

        boolean removedAny;
        SearchForIntersectingInterval: do {
            removedAny = false;
            final int listSize = newAffectedMiddleRangeList.size();
            for (int i = 0; i < listSize; i++) {
                for (int j = i + 1; j < listSize; j++) {
                    Pair<Integer, Integer> leftInterval = newAffectedMiddleRangeList.get(i);
                    Pair<Integer, Integer> rightInterval = newAffectedMiddleRangeList.get(j);

                    if (leftInterval.getKey() <= rightInterval.getValue() &&
                            rightInterval.getKey() <= leftInterval.getValue()) {
                        Pair<Integer, Integer> mergedInterval =
                                Pair.of(Math.min(leftInterval.getKey(), rightInterval.getKey()),
                                        Math.max(leftInterval.getValue(), rightInterval.getValue()));
                        newAffectedMiddleRangeList.set(i, mergedInterval);
                        newAffectedMiddleRangeList.remove(j);
                        removedAny = true;
                        continue SearchForIntersectingInterval;
                    }
                }
            }
        } while (removedAny);

        return new KOptAffectedElements(newWrappedStartIndex, newWrappedEndIndex, newAffectedMiddleRangeList);
    }

    @Override
    public String toString() {
        return "KOptAffectedElementsInfo{" +
                "wrappedStartIndex=" + wrappedStartIndex +
                ", wrappedEndIndex=" + wrappedEndIndex +
                ", affectedMiddleRangeList=" + affectedMiddleRangeList +
                '}';
    }
}
