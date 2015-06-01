/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investmentallocation.solver.move.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.solver.move.CheapTimePillarSlideMove;
import org.optaplanner.examples.investmentallocation.domain.AssetClassAllocation;
import org.optaplanner.examples.investmentallocation.domain.InvestmentAllocationSolution;
import org.optaplanner.examples.investmentallocation.domain.util.InvestmentAllocationMicrosUtil;
import org.optaplanner.examples.investmentallocation.solver.move.InvestmentQuantityTransferMove;

public class InvestmentQuantityTransferMoveIteratorFactory implements MoveIteratorFactory {

    @Override
    public long getSize(ScoreDirector scoreDirector) {
        InvestmentAllocationSolution solution = (InvestmentAllocationSolution) scoreDirector.getWorkingSolution();
        return (solution.getAssetClassAllocationList().size() - 1)
                * InvestmentAllocationMicrosUtil.MAXIMUM_QUANTITY_MICROS;
    }

    @Override
    public Iterator<Move> createOriginalMoveIterator(ScoreDirector scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Move> createRandomMoveIterator(ScoreDirector scoreDirector, Random workingRandom) {
        InvestmentAllocationSolution solution = (InvestmentAllocationSolution) scoreDirector.getWorkingSolution();
        List<AssetClassAllocation> allocationList = solution.getAssetClassAllocationList();
        NavigableMap<Long, AssetClassAllocation> quantityIncrementMicrosToAllocationMap = new TreeMap<Long, AssetClassAllocation>();
        long quantityIncrementMicros = 0L;
        for (AssetClassAllocation allocation : allocationList) {
            quantityIncrementMicros +=  allocation.getQuantityMicros();
            quantityIncrementMicrosToAllocationMap.put(quantityIncrementMicros, allocation);
        }
        if (quantityIncrementMicros != InvestmentAllocationMicrosUtil.MAXIMUM_QUANTITY_MICROS) {
            throw new IllegalStateException("The quantityIncrementMicros (" + quantityIncrementMicros
                    + ") must always be total to MAXIMUM_QUANTITY_MICROS ("
                    + InvestmentAllocationMicrosUtil.MAXIMUM_QUANTITY_MICROS + ").");
        }
        return new RandomInvestmentQuantityTransferMoveIterator(allocationList, quantityIncrementMicrosToAllocationMap, workingRandom);
    }

    private class RandomInvestmentQuantityTransferMoveIterator implements Iterator<Move> {

        private final List<AssetClassAllocation> allocationList;
        private final NavigableMap<Long, AssetClassAllocation> quantityIncrementMicrosToAllocationMap;
        private final Random workingRandom;

        public RandomInvestmentQuantityTransferMoveIterator(List<AssetClassAllocation> allocationList,
                NavigableMap<Long, AssetClassAllocation> quantityIncrementMicrosToAllocationMap, Random workingRandom) {
            this.allocationList = allocationList;
            this.quantityIncrementMicrosToAllocationMap = quantityIncrementMicrosToAllocationMap;
            this.workingRandom = workingRandom;
        }

        public boolean hasNext() {
            return allocationList.size() > 1;
        }

        public Move next() {
            long transferMicros
                    = RandomUtils.nextLong(workingRandom, InvestmentAllocationMicrosUtil.MAXIMUM_QUANTITY_MICROS) + 1L;
            Map.Entry<Long, AssetClassAllocation> lowerEntry
                    = quantityIncrementMicrosToAllocationMap.lowerEntry(transferMicros);
            Map.Entry<Long, AssetClassAllocation> ceilingEntry = quantityIncrementMicrosToAllocationMap
                    .ceilingEntry(transferMicros);
            transferMicros -= (lowerEntry == null ? 0L : lowerEntry.getKey());
            AssetClassAllocation fromAllocation = ceilingEntry.getValue();

            // TODO improve scalability by not using indexOf() on an ArrayList
            int fromAllocationIndex = allocationList.indexOf(fromAllocation);
            int toAllocationIndex = workingRandom.nextInt(allocationList.size() - 1);
            if (toAllocationIndex >= fromAllocationIndex) {
                toAllocationIndex++;
            }
            AssetClassAllocation toAllocation = allocationList.get(toAllocationIndex);
            return new InvestmentQuantityTransferMove(fromAllocation, toAllocation, transferMicros);
        }

        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

    }

}
