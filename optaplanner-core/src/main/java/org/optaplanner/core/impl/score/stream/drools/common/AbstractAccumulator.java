/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

abstract class AbstractAccumulator<ResultContainer_, Result_> implements Accumulator {

    private final Supplier<ResultContainer_> containerSupplier;
    private final Function<ResultContainer_, Result_> finisher;

    protected AbstractAccumulator(Supplier<ResultContainer_> containerSupplier,
            Function<ResultContainer_, Result_> finisher) {
        this.containerSupplier = Objects.requireNonNull(containerSupplier);
        this.finisher = Objects.requireNonNull(finisher);
    }

    protected static <Value_> Value_ extractValue(Declaration declaration, int offset, Tuple leftTuple) {
        Tuple tuple = getTuple(offset, leftTuple);
        return (Value_) declaration.getValue(null, tuple.getFactHandle().getObject());
    }

    protected static int findTupleOffset(Declaration declaration, Tuple tuple) {
        int offset = 0;
        while (tuple.getIndex() != declaration.getTupleIndex()) {
            tuple = tuple.getParent();
            offset++;
        }
        return offset;
    }

    protected static Tuple getTuple(int deltaOffset, Tuple tuple) {
        switch (deltaOffset) {
            case 0:
                return tuple;
            case 1:
                return tuple.getParent();
            case 2:
                return tuple.getParent()
                        .getParent();
            case 3:
                return tuple.getParent()
                        .getParent()
                        .getParent();
            default:
                throw new UnsupportedOperationException("Impossible state: tuple delta offset (" + deltaOffset + ").");
        }
    }

    @Override
    public final Object createWorkingMemoryContext() {
        return null;
    }

    @Override
    public final Object createContext() {
        return null; // We always create and init during init(...).
    }

    @Override
    public final ResultContainer_ init(Object workingMemoryContext, Object context, Tuple leftTuple,
            Declaration[] declarations, ReteEvaluator reteEvaluator) {
        return containerSupplier.get();
    }

    @Override
    public final boolean supportsReverse() {
        return true;
    }

    @Override
    public final boolean tryReverse(Object workingMemoryContext, Object context, Tuple leftTuple,
            InternalFactHandle handle, Object value, Declaration[] declarations, Declaration[] innerDeclarations,
            ReteEvaluator reteEvaluator) {
        ((Runnable) value).run();
        return true;
    }

    @Override
    public Result_ getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations,
            ReteEvaluator reteEvaluator) {
        return finisher.apply((ResultContainer_) context);
    }
}
