package org.optaplanner.core.impl.heuristic.selector.list;

import static org.optaplanner.core.impl.heuristic.selector.list.TriangularNumbers.nthTriangle;
import static org.optaplanner.core.impl.heuristic.selector.list.TriangularNumbers.triangularRoot;

import java.util.Random;

final class TriangleElementFactory {

    private final int minimumSubListSize;
    private final int maximumSubListSize;
    private final Random workingRandom;

    TriangleElementFactory(int minimumSubListSize, int maximumSubListSize, Random workingRandom) {
        if (minimumSubListSize > maximumSubListSize) {
            throw new IllegalArgumentException("The minimumSubListSize (" + minimumSubListSize
                    + ") must be less than or equal to the maximumSubListSize (" + maximumSubListSize + ").");
        }
        if (minimumSubListSize < 1) {
            throw new IllegalArgumentException(
                    "The minimumSubListSize (" + minimumSubListSize + ") must be greater than 0.");
        }
        this.minimumSubListSize = minimumSubListSize;
        this.maximumSubListSize = maximumSubListSize;
        this.workingRandom = workingRandom;
    }

    /**
     * Produce next random element of Triangle(listSize) observing the given minimum and maximum subList size.
     *
     * @param listSize determines the Triangle to select an element from
     * @return next random triangle element
     * @throws IllegalArgumentException if {@code listSize} is less than {@code minimumSubListSize}
     */
    TriangleElement nextElement(int listSize) throws IllegalArgumentException {
        // Reduce the triangle base by the minimum subList size.
        int subListCount = nthTriangle(listSize - minimumSubListSize + 1);
        // The top triangle represents all subLists of size greater or equal to maximum subList size. Remove them all.
        int topTriangleSize = listSize <= maximumSubListSize ? 0 : nthTriangle(listSize - maximumSubListSize);
        // Triangle elements are indexed from 1.
        int subListIndex = workingRandom.nextInt(subListCount - topTriangleSize) + topTriangleSize + 1;
        return TriangleElement.valueOf(subListIndex);
    }

    static final class TriangleElement {

        private final int index;
        private final int level;
        private final int indexOnLevel;

        private TriangleElement(int index, int level, int indexOnLevel) {
            this.index = index;
            this.level = level;
            this.indexOnLevel = indexOnLevel;
        }

        static TriangleElement valueOf(int index) {
            int level = (int) Math.ceil(triangularRoot(index));
            return new TriangleElement(index, level, index - nthTriangle(level - 1));
        }

        public int getIndex() {
            return index;
        }

        public int getLevel() {
            return level;
        }

        public int getIndexOnLevel() {
            return indexOnLevel;
        }
    }
}
