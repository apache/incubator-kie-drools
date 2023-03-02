package org.drools.core.util.index;

import java.lang.reflect.InvocationTargetException;

import org.drools.core.reteoo.TupleMemory;

public class IndexMemory {

    private static final String INTERNAL_INDEX = "internal";
    private static final String FASTUTIL_INDEX = "fastutil";

    private static final String DEFAULT_INDEX = INTERNAL_INDEX;

    public enum EqualityMemoryType {
        INTERNAL, FASTUTIL;

        static EqualityMemoryType get(String s) {
            if (s.equalsIgnoreCase(FASTUTIL_INDEX)) {
                return FASTUTIL;
            }
            return INTERNAL;
        }

        Factory createFactory() {
            if (this == FASTUTIL) {
                try {
                    return (Factory) Class.forName("org.drools.fastutil.FastUtilIndexMemory.FastUtilEqualityMemoryFactory").getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                    throw new RuntimeException("You're trying to use fastutil indexes without having imported them. Please add the module org.drools:drools-fastutil to your classpath.", e);
                }
            }
            return new InternalEqualityMemoryFactory();
        }
    }

    public enum ComparisonMemoryType {
        INTERNAL, FASTUTIL;

        static ComparisonMemoryType get(String s) {
            if (s.equalsIgnoreCase(FASTUTIL_INDEX)) {
                return FASTUTIL;
            }
            return INTERNAL;
        }

        Factory createFactory() {
            if (this == FASTUTIL) {
                try {
                    return (Factory) Class.forName("org.drools.fastutil.FastUtilIndexMemory.FastUtilComparisonMemoryFactory").getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                    throw new RuntimeException("You're trying to use fastutil indexes without having imported them. Please add the module org.drools:drools-fastutil to your classpath.", e);
                }
            }
            return new InternalComparisonMemoryFactory();
        }
    }

    private static EqualityMemoryType EQUALITY_MEMORY_TYPE; // did not set this as final, as some tests need to change this

    private static ComparisonMemoryType COMPARISON_MEMORY_TYPE; // did not set this as final, as some tests need to change this

    static {
        EQUALITY_MEMORY_TYPE = EqualityMemoryType.get(System.getProperty("org.drools.equalitymemory", DEFAULT_INDEX));
        COMPARISON_MEMORY_TYPE = ComparisonMemoryType.get(System.getProperty("org.drools.comparisonmemory", DEFAULT_INDEX));
    }

    public static EqualityMemoryType getEqualityMemoryType() {
        return EQUALITY_MEMORY_TYPE;
    }

    public static void setEqualityMemoryType(EqualityMemoryType type) {
        EQUALITY_MEMORY_TYPE = type;
        EqualityMemoryFactoryHolder.reinit();
    }

    public static ComparisonMemoryType getComparisonMemoryType() {
        return COMPARISON_MEMORY_TYPE;
    }

    public static void setComparisonMemoryType(ComparisonMemoryType type) {
        COMPARISON_MEMORY_TYPE = type;
        ComparisonMemoryFactoryHolder.reinit();
    }

    public static TupleMemory createEqualityMemory(IndexSpec indexSpec, boolean isLeft) {
        return EqualityMemoryFactoryHolder.INSTANCE.createMemory(indexSpec, isLeft);
    }

    public static TupleMemory createComparisonMemory(IndexSpec indexSpec, boolean isLeft) {
        return ComparisonMemoryFactoryHolder.INSTANCE.createMemory(indexSpec, isLeft);
    }

    public interface Factory {
        TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft);
    }

    static class EqualityMemoryFactoryHolder {
        private static Factory INSTANCE = EQUALITY_MEMORY_TYPE.createFactory(); // did not set this as final, as some tests need to change this

        private static void reinit() {
            INSTANCE = EQUALITY_MEMORY_TYPE.createFactory();
        }
    }

    static class ComparisonMemoryFactoryHolder {
        private static Factory INSTANCE = COMPARISON_MEMORY_TYPE.createFactory(); // did not set this as final, as some tests need to change this

        private static void reinit() {
            INSTANCE = COMPARISON_MEMORY_TYPE.createFactory();
        }
    }

    static class InternalEqualityMemoryFactory implements IndexMemory.Factory {

        @Override
        public TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft) {
            return new TupleIndexHashTable(indexSpec.getIndexes(), isLeft);
        }
    }

    static class InternalComparisonMemoryFactory implements IndexMemory.Factory {

        @Override
        public TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft) {
            return new TupleIndexRBTree(indexSpec.getConstraintType(), indexSpec.getIndex(0), isLeft);
        }
    }
}
