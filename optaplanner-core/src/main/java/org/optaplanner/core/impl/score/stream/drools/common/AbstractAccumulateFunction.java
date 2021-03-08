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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.api.runtime.rule.AccumulateFunction;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

/**
 * Adapts a CS constraint collector to {@link AccumulateFunction}.
 *
 * <p>
 * {@link AccumulateFunction} has two behaviors:
 *
 * <ul>
 * <li>First, using the {@link #accumulate(Object, Object)} method, it returns the accumulated object which is
 * later consumed by {@link #reverse(AccumulateContext, Object)}.</li>
 * <li>Second, using the {@link #accumulateValue(AccumulateContext, Object)} method,
 * it allows us to provide a custom return object which is later consumed
 * by {@link #tryReverse(AccumulateContext, Object)}.</li>
 * </ul>
 *
 * Using the second method, we get to return the accumulate undo directly,
 * as opposed to keeping it in our own undo map if we had been using the first method.
 *
 * <p>
 * That is why this class overrides {@link #accumulateValue(AccumulateContext, Object)} and
 * {@link #tryReverse(AccumulateContext, Object)},
 * instead of using {@link #accumulate(AccumulateContext, Object)} and {@link #reverse(AccumulateContext, Object)}
 * as the {@link AccumulateFunction} interface intends.
 *
 * <p>
 * The two methods we do not use would have normally been called from the methods we do use.
 * Therefore we can afford to have the former throw {@link UnsupportedOperationException},
 * as there is no code to call them now.
 *
 * @param <ResultContainer_> result container of the constraint collector
 * @param <InTuple> input type of the accumulate function (plain "A" for Uni, {@link BiTuple}<A, B> for Bi etc.)
 * @param <OutTuple> output of the constraint collector
 */
abstract class AbstractAccumulateFunction<ResultContainer_, InTuple, OutTuple>
        implements AccumulateFunction<AbstractAccumulateFunction.AccumulateContext<ResultContainer_>> {

    private final Supplier<ResultContainer_> containerSupplier;
    private final Function<ResultContainer_, OutTuple> finisher;

    protected AbstractAccumulateFunction(Supplier<ResultContainer_> containerSupplier,
            Function<ResultContainer_, OutTuple> finisher) {
        this.containerSupplier = Objects.requireNonNull(containerSupplier);
        this.finisher = Objects.requireNonNull(finisher);
    }

    @Override
    public AccumulateContext<ResultContainer_> createContext() {
        return new AccumulateContext<>(containerSupplier);
    }

    @Override
    public void init(AccumulateContext<ResultContainer_> context) {
        context.reset();
    }

    @Override
    public void accumulate(AccumulateContext<ResultContainer_> context, Object value) {
        throw new UnsupportedOperationException(); // Handled in accumulateValue().
    }

    @Override
    public Runnable accumulateValue(AccumulateContext<ResultContainer_> context, Object value) {
        return accumulate(context.getContainer(), (InTuple) value); // Returns the undo directly.
    }

    protected abstract Runnable accumulate(ResultContainer_ container, InTuple tuple);

    @Override
    public void reverse(AccumulateContext<ResultContainer_> context, Object value) {
        throw new UnsupportedOperationException(); // Handled in tryReverse().
    }

    @Override
    public boolean tryReverse(AccumulateContext<ResultContainer_> context, Object value) {
        ((Runnable) value).run(); // The value is the undo as returned by accumulateValue().
        return true;
    }

    @Override
    public OutTuple getResult(AccumulateContext<ResultContainer_> context) {
        return finisher.apply(context.getContainer());
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Class<?> getResultType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readExternal(ObjectInput in) {
        throw new UnsupportedOperationException();
    }

    /**
     * Each context is uniquely identified by its {@link System#identityHashCode(Object)}.
     * This is a dummy {@link Serializable} wrapper class
     * as required by {@link org.drools.model.functions.accumulate.AccumulateFunction}.
     *
     * @param <ResultContainer_> The same type from {@link UniConstraintCollector} and its Bi, Tri, ... alternatives.
     */
    static final class AccumulateContext<ResultContainer_> implements Serializable {

        private final Supplier<ResultContainer_> containerSupplier;
        private final AtomicReference<ResultContainer_> containerReference = new AtomicReference<>();

        public AccumulateContext(Supplier<ResultContainer_> containerSupplier) {
            this.containerSupplier = containerSupplier;
        }

        public ResultContainer_ getContainer() {
            return containerReference.updateAndGet(container -> (container == null) ? containerSupplier.get() : container);
        }

        public void reset() {
            this.containerReference.set(null);
        }

    }

}
