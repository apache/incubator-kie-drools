package org.drools.fastutil;

import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.index.IndexMemory;
import org.drools.core.util.index.IndexSpec;

public class FastUtilIndexMemory {

    public static class FastUtilEqualityMemoryFactory implements IndexMemory.Factory {

        @Override
        public TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft) {
            return new FastUtilHashTupleMemory(indexSpec.getIndexes(), isLeft);
        }
    }

    public static class FastUtilComparisonMemoryFactory implements IndexMemory.Factory {

        @Override
        public TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft) {
            return new FastUtilTreeMemory(indexSpec.getConstraintType(), indexSpec.getIndex(0), isLeft);
        }
    }
}
