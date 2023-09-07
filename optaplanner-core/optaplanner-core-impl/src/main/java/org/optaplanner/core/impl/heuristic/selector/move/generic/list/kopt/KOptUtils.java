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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.util.Pair;

final class KOptUtils {

    private KOptUtils() {
    }

    /**
     * Calculate the disjoint k-cycles for {@link KOptDescriptor#getRemovedEdgeIndexToTourOrder()}. <br />
     * <br />
     * Any permutation can be expressed as combination of k-cycles. A k-cycle is a sequence of
     * unique elements (p_1, p_2, ..., p_k) where
     * <ul>
     * <li>p_1 maps to p_2 in the permutation</li>
     * <li>p_2 maps to p_3 in the permutation</li>
     * <li>p_(k-1) maps to p_k in the permutation</li>
     * <li>p_k maps to p_1 in the permutation</li>
     * <li>In general: p_i maps to p_(i+1) in the permutation</li>
     * </ul>
     * For instance, the permutation
     * <ul>
     * <li>1 -> 2</li>
     * <li>2 -> 3</li>
     * <li>3 -> 1</li>
     * <li>4 -> 5</li>
     * <li>5 -> 4</li>
     * </ul>
     * can be expressed as `(1, 2, 3)(4, 5)`.
     *
     * @return The {@link KOptCycle} corresponding to the permutation described by
     *         {@link KOptDescriptor#getRemovedEdgeIndexToTourOrder()}.
     * @param kOptDescriptor The descriptor to calculate cycles for
     */
    static KOptCycle getCyclesForPermutation(KOptDescriptor<?> kOptDescriptor) {
        int cycleCount = 0;
        int[] removedEdgeIndexToTourOrder = kOptDescriptor.getRemovedEdgeIndexToTourOrder();
        int[] addedEdgeToOtherEndpoint = kOptDescriptor.getAddedEdgeToOtherEndpoint();
        int[] inverseRemovedEdgeIndexToTourOrder = kOptDescriptor.getInverseRemovedEdgeIndexToTourOrder();

        int[] indexToCycle = new int[removedEdgeIndexToTourOrder.length];
        BitSet remaining = new BitSet(removedEdgeIndexToTourOrder.length);
        remaining.set(1, removedEdgeIndexToTourOrder.length, true);

        while (!remaining.isEmpty()) {
            int currentEndpoint = remaining.nextSetBit(0);
            while (remaining.get(currentEndpoint)) {
                indexToCycle[currentEndpoint] = cycleCount;
                remaining.clear(currentEndpoint);

                // Go to the endpoint connected to this one by an added edge
                int currentEndpointTourIndex = removedEdgeIndexToTourOrder[currentEndpoint];
                int nextEndpointTourIndex = addedEdgeToOtherEndpoint[currentEndpointTourIndex];
                currentEndpoint = inverseRemovedEdgeIndexToTourOrder[nextEndpointTourIndex];

                indexToCycle[currentEndpoint] = cycleCount;
                remaining.clear(currentEndpoint);

                // Go to the endpoint after the added edge
                currentEndpoint = currentEndpoint ^ 1;
            }
            cycleCount++;
        }
        return new KOptCycle(cycleCount, indexToCycle);
    }

    static <Node_> List<Pair<Node_, Node_>> getAddedEdgeList(KOptDescriptor<Node_> kOptDescriptor) {
        int k = kOptDescriptor.getK();
        List<Pair<Node_, Node_>> out = new ArrayList<>(2 * k);
        int currentEndpoint = 1;

        Node_[] removedEdges = kOptDescriptor.getRemovedEdges();
        int[] addedEdgeToOtherEndpoint = kOptDescriptor.getAddedEdgeToOtherEndpoint();
        int[] removedEdgeIndexToTourOrder = kOptDescriptor.getRemovedEdgeIndexToTourOrder();
        int[] inverseRemovedEdgeIndexToTourOrder = kOptDescriptor.getInverseRemovedEdgeIndexToTourOrder();

        // This loop iterates through the new tour created
        while (currentEndpoint != 2 * k + 1) {
            out.add(Pair.of(removedEdges[currentEndpoint], removedEdges[addedEdgeToOtherEndpoint[currentEndpoint]]));
            int tourIndex = removedEdgeIndexToTourOrder[currentEndpoint];
            int nextEndpointTourIndex = addedEdgeToOtherEndpoint[tourIndex];
            currentEndpoint = inverseRemovedEdgeIndexToTourOrder[nextEndpointTourIndex] ^ 1;
        }
        return out;
    }

    static <Node_> List<Pair<Node_, Node_>> getRemovedEdgeList(KOptDescriptor<Node_> kOptDescriptor) {
        int k = kOptDescriptor.getK();
        Node_[] removedEdges = kOptDescriptor.getRemovedEdges();
        List<Pair<Node_, Node_>> out = new ArrayList<>(2 * k);
        for (int i = 1; i <= k; i++) {
            out.add(Pair.of(removedEdges[2 * i - 1], removedEdges[2 * i]));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <Node_> Function<Node_, Node_> getSuccessorFunction(
            ListVariableDescriptor<?> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply) {
        return (node) -> {
            List<Node_> valueList =
                    (List<Node_>) listVariableDescriptor.getListVariable(inverseVariableSupply.getInverseSingleton(node));
            int index = indexVariableSupply.getIndex(node);
            if (index == valueList.size() - 1) {
                return valueList.get(0);
            } else {
                return valueList.get(index + 1);
            }
        };
    }

    public static <Node_> Function<Node_, Node_> getMultiEntitySuccessorFunction(
            Node_[] pickedValues,
            ListVariableDescriptor<?> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply) {
        EntityOrderInfo entityOrderInfo = new EntityOrderInfo(pickedValues, inverseVariableSupply, listVariableDescriptor);
        return node -> entityOrderInfo.successor(node, listVariableDescriptor, indexVariableSupply, inverseVariableSupply);
    }

    public static <Node_> TriPredicate<Node_, Node_, Node_> getBetweenPredicate(IndexVariableSupply indexVariableSupply) {
        return (start, middle, end) -> {
            int startIndex = indexVariableSupply.getIndex(start);
            int middleIndex = indexVariableSupply.getIndex(middle);
            int endIndex = indexVariableSupply.getIndex(end);

            if (startIndex <= endIndex) {
                // test middleIndex in [startIndex, endIndex]
                return startIndex <= middleIndex && middleIndex <= endIndex;
            } else {
                // test middleIndex in [0, endIndex] or middleIndex in [startIndex, listSize)
                return middleIndex >= startIndex || middleIndex <= endIndex;
            }
        };
    }

    public static <Node_> TriPredicate<Node_, Node_, Node_> getMultiEntityBetweenPredicate(Node_[] pickedValues,
            ListVariableDescriptor<?> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply) {
        EntityOrderInfo entityOrderInfo = new EntityOrderInfo(pickedValues, inverseVariableSupply, listVariableDescriptor);
        return (start, middle, end) -> entityOrderInfo.between(start, middle, end, indexVariableSupply, inverseVariableSupply);
    }

    public static void flipSubarray(int[] array, int fromIndexInclusive, int toIndexExclusive) {
        if (fromIndexInclusive < toIndexExclusive) {
            final int halfwayPoint = (toIndexExclusive - fromIndexInclusive) >> 1;
            for (int i = 0; i < halfwayPoint; i++) {
                int saved = array[fromIndexInclusive + i];
                array[fromIndexInclusive + i] = array[toIndexExclusive - i - 1];
                array[toIndexExclusive - i - 1] = saved;
            }
        } else {
            int firstHalfSize = array.length - fromIndexInclusive;
            int secondHalfSize = toIndexExclusive;

            // Reverse the combined list firstHalfReversedPath + secondHalfReversedPath
            // For instance, (1, 2, 3)(4, 5, 6, 7, 8, 9) becomes
            // (9, 8, 7)(6, 5, 4, 3, 2, 1)
            int totalLength = firstHalfSize + secondHalfSize;

            // Used to rotate the list to put the first element back in its original position
            for (int i = 0; (i < totalLength >> 1); i++) {
                int firstHalfIndex;
                int secondHalfIndex;

                if (i < firstHalfSize) {
                    if (i < secondHalfSize) {
                        firstHalfIndex = fromIndexInclusive + i;
                        secondHalfIndex = secondHalfSize - i - 1;
                    } else {
                        firstHalfIndex = fromIndexInclusive + i;
                        secondHalfIndex = array.length - (i - secondHalfSize) - 1;
                    }
                } else {
                    firstHalfIndex = i - firstHalfSize;
                    secondHalfIndex = secondHalfSize - i - 1;
                }

                int saved = array[firstHalfIndex];
                array[firstHalfIndex] = array[secondHalfIndex];
                array[secondHalfIndex] = saved;
            }
        }
    }

    /**
     * Returns the number of unique ways a K-Opt can add K edges without reinserting a removed edge.
     *
     * @param k How many edges were removed/will be added
     * @return the number of unique ways a K-Opt can add K edges without reinserting a removed edge.
     */
    public static long getPureKOptMoveTypes(int k) {
        // This calculates the item at index k for the sequence https://oeis.org/A061714
        long totalTypes = 0;
        for (int i = 1; i < k; i++) {
            for (int j = 0; j <= i; j++) {
                int sign = ((k + j - 1) % 2 == 0) ? 1 : -1;
                totalTypes += sign * CombinatoricsUtils.binomialCoefficient(i, j) * CombinatoricsUtils.factorial(j) * (1L << j);
            }
        }
        return totalTypes;
    }
}
