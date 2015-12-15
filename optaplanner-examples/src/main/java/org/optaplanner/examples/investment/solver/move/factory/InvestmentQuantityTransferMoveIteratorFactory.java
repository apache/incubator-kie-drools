/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.investment.solver.move.factory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;
import org.optaplanner.examples.investment.solver.move.InvestmentQuantityTransferMove;

public class InvestmentQuantityTransferMoveIteratorFactory implements MoveIteratorFactory {

    @Override
    public long getSize(ScoreDirector scoreDirector) {
        InvestmentSolution solution = (InvestmentSolution) scoreDirector.getWorkingSolution();
        int size = solution.getAssetClassAllocationList().size();
        // The MAXIMUM_QUANTITY_MILLIS accounts for all fromAllocations too
        return InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS * (size - 1);
    }

    @Override
    public Iterator<Move> createOriginalMoveIterator(ScoreDirector scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Move> createRandomMoveIterator(ScoreDirector scoreDirector, Random workingRandom) {
        InvestmentSolution solution = (InvestmentSolution) scoreDirector.getWorkingSolution();
        List<AssetClassAllocation> allocationList = solution.getAssetClassAllocationList();
        NavigableMap<Long, AssetClassAllocation> quantityMillisIncrementToAllocationMap = new TreeMap<Long, AssetClassAllocation>();
        long quantityIncrementMillis = 0L;
        for (AssetClassAllocation allocation : allocationList) {
            long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis > 0L) {
                quantityIncrementMillis += quantityMillis;
                quantityMillisIncrementToAllocationMap.put(quantityIncrementMillis, allocation);
            }
        }
        if (quantityIncrementMillis != InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS) {
            throw new IllegalStateException("The quantityIncrementMillis (" + quantityIncrementMillis
                    + ") must always be total to MAXIMUM_QUANTITY_MILLIS ("
                    + InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS + ").");
        }
        return new RandomInvestmentQuantityTransferMoveIterator(allocationList,
                quantityMillisIncrementToAllocationMap, workingRandom);
    }

    private class RandomInvestmentQuantityTransferMoveIterator implements Iterator<Move> {

        private final List<AssetClassAllocation> allocationList;
        private final NavigableMap<Long, AssetClassAllocation> quantityMillisIncrementToAllocationMap;
        private final Random workingRandom;

        public RandomInvestmentQuantityTransferMoveIterator(List<AssetClassAllocation> allocationList,
                NavigableMap<Long, AssetClassAllocation> quantityMillisIncrementToAllocationMap, Random workingRandom) {
            this.allocationList = allocationList;
            this.quantityMillisIncrementToAllocationMap = quantityMillisIncrementToAllocationMap;
            this.workingRandom = workingRandom;
        }

        public boolean hasNext() {
            return allocationList.size() >= 2;
        }

        public Move next() {
            long transferMillis
                    = RandomUtils.nextLong(workingRandom, InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS) + 1L;
            Map.Entry<Long, AssetClassAllocation> lowerEntry
                    = quantityMillisIncrementToAllocationMap.lowerEntry(transferMillis);
            Map.Entry<Long, AssetClassAllocation> ceilingEntry = quantityMillisIncrementToAllocationMap
                    .ceilingEntry(transferMillis);
            transferMillis -= (lowerEntry == null ? 0L : lowerEntry.getKey());
            AssetClassAllocation fromAllocation = ceilingEntry.getValue();

            AssetClassAllocation toAllocation = allocationList.get(workingRandom.nextInt(allocationList.size() - 1));
            if (toAllocation == fromAllocation) {
                toAllocation = allocationList.get(allocationList.size() - 1);
            }
            return new InvestmentQuantityTransferMove(fromAllocation, toAllocation, transferMillis);
        }

        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

    }

}
