package org.optaplanner.examples.investment.solver.move.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;
import org.optaplanner.examples.investment.solver.move.InvestmentQuantityTransferMove;

public class InvestmentBiQuantityTransferMoveIteratorFactory
        implements MoveIteratorFactory<InvestmentSolution, Move<InvestmentSolution>> {

    @Override
    public long getSize(ScoreDirector<InvestmentSolution> scoreDirector) {
        InvestmentSolution solution = scoreDirector.getWorkingSolution();
        int size = solution.getAssetClassAllocationList().size();
        // The MAXIMUM_QUANTITY_MILLIS accounts for all fromAllocations too
        return InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS
                * InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS
                * (size - 1) * (size - 1);
    }

    @Override
    public Iterator<Move<InvestmentSolution>> createOriginalMoveIterator(
            ScoreDirector<InvestmentSolution> scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Move<InvestmentSolution>> createRandomMoveIterator(
            ScoreDirector<InvestmentSolution> scoreDirector, Random workingRandom) {
        InvestmentSolution solution = scoreDirector.getWorkingSolution();
        List<AssetClassAllocation> allocationList = solution.getAssetClassAllocationList();
        List<AssetClassAllocation> nonEmptyAllocationList = new ArrayList<>(allocationList);
        nonEmptyAllocationList.removeIf(allocation -> allocation.getQuantityMillis() == 0L);
        return new RandomInvestmentBiQuantityTransferMoveIterator(allocationList,
                nonEmptyAllocationList, workingRandom);
    }

    private static class RandomInvestmentBiQuantityTransferMoveIterator implements Iterator<Move<InvestmentSolution>> {

        private final List<AssetClassAllocation> allocationList;
        private final List<AssetClassAllocation> nonEmptyAllocationList;
        private final Random workingRandom;

        public RandomInvestmentBiQuantityTransferMoveIterator(List<AssetClassAllocation> allocationList,
                List<AssetClassAllocation> nonEmptyAllocationList, Random workingRandom) {
            this.allocationList = allocationList;
            this.nonEmptyAllocationList = nonEmptyAllocationList;
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return allocationList.size() >= 3 && nonEmptyAllocationList.size() >= 1;
        }

        @Override
        public Move<InvestmentSolution> next() {
            AssetClassAllocation firstFrom;
            AssetClassAllocation secondFrom;
            int nonEmptyAllocationListSize = nonEmptyAllocationList.size();
            if (nonEmptyAllocationListSize == 1) {
                firstFrom = nonEmptyAllocationList.get(0);
                secondFrom = firstFrom;
            } else {
                firstFrom = nonEmptyAllocationList.get(workingRandom.nextInt(nonEmptyAllocationListSize));
                // secondFrom can be the same as firstFrom, for example in a split from 1 into 2 others
                secondFrom = nonEmptyAllocationList.get(workingRandom.nextInt(nonEmptyAllocationListSize));
            }
            int allocationListSize = allocationList.size();
            int toCandidateSize = allocationListSize - (firstFrom == secondFrom ? 1 : 2);
            AssetClassAllocation firstTo = allocationList.get(workingRandom.nextInt(toCandidateSize));
            if (firstTo == firstFrom) {
                firstTo = allocationList.get(allocationListSize - 1);
            } else if (firstTo == secondFrom) {
                firstTo = allocationList.get(allocationListSize - 2);
            }
            // secondTo can be the same as firstTo, for example in a merge from 2 others into 1
            AssetClassAllocation secondTo = allocationList.get(workingRandom.nextInt(toCandidateSize));
            if (secondTo == firstFrom) {
                secondTo = allocationList.get(allocationListSize - 1);
            } else if (secondTo == secondFrom) {
                secondTo = allocationList.get(allocationListSize - 2);
            }
            long firstTransferMillis = nextLong(workingRandom, firstFrom.getQuantityMillis()) + 1L;
            if (firstFrom == secondFrom && firstFrom.getQuantityMillis() == firstTransferMillis) {
                // secondTransferMillis must never do a nextLong(0L) which would throw an IllegalArgumentException
                firstTransferMillis--;
            }
            long secondTransferMillis = nextLong(workingRandom, secondFrom.getQuantityMillis()
                    - (firstFrom == secondFrom ? firstTransferMillis : 0L)) + 1L;
            return CompositeMove.buildMove(new InvestmentQuantityTransferMove(firstFrom, firstTo, firstTransferMillis),
                    new InvestmentQuantityTransferMove(secondFrom, secondTo, secondTransferMillis));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

    }

    public static long nextLong(Random random, long n) {
        // This code is based on java.util.Random#nextInt(int)'s javadoc.
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (n < Integer.MAX_VALUE) {
            return random.nextInt((int) n);
        }

        long bits;
        long val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }

}
