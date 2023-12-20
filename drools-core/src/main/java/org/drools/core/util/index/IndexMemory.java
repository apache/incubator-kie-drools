/**
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
package org.drools.core.util.index;

import java.lang.reflect.InvocationTargetException;

import org.drools.core.reteoo.TupleMemory;

import static org.drools.util.Config.getConfig;

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
        EQUALITY_MEMORY_TYPE = EqualityMemoryType.get(getConfig("org.drools.equalitymemory", DEFAULT_INDEX));
        COMPARISON_MEMORY_TYPE = ComparisonMemoryType.get(getConfig("org.drools.comparisonmemory", DEFAULT_INDEX));
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
            return new TupleIndexHashTable(indexSpec.getIndex(), isLeft);
        }
    }

    static class InternalComparisonMemoryFactory implements IndexMemory.Factory {

        @Override
        public TupleMemory createMemory(IndexSpec indexSpec, boolean isLeft) {
            return new TupleIndexRBTree(indexSpec.getConstraintType(), indexSpec.getIndex(0), isLeft);
        }
    }
}
