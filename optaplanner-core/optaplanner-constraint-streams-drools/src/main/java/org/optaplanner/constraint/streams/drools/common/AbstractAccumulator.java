package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.accessor.Accumulator;

abstract class AbstractAccumulator<ResultContainer_, Result_> implements Accumulator {

    private final Supplier<ResultContainer_> containerSupplier;
    private final Function<ResultContainer_, Result_> finisher;

    private volatile boolean initialized = false;

    protected AbstractAccumulator(Supplier<ResultContainer_> containerSupplier,
            Function<ResultContainer_, Result_> finisher) {
        this.containerSupplier = Objects.requireNonNull(containerSupplier);
        this.finisher = Objects.requireNonNull(finisher);
    }

    protected static <X> Function<Tuple, X> getValueExtractor(Declaration declaration, Tuple leftTuple) {
        return new ValueExtractor<>(declaration, leftTuple);
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
    public final Object accumulate(Object workingMemoryContext, Object context, Tuple leftTuple,
            InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations,
            ReteEvaluator reteEvaluator) {
        /*
         * Accumulator instances are created within the KieBase, not within the KieSession.
         * This means that multiple sessions from the same KieBase may be calling this method
         * on a shared accumulator from different threads.
         * (Multi-threaded solving, moveThreadCount > 1.)
         *
         * The logical place for this call would be the init(...) method of the Accumulator interface.
         * Unfortunately, we need access to innerDeclarations to be able to perform the accumulation quickly,
         * and it only becomes available when this accumulate(...) method is called.
         * Therefore, initialization of our accumulator can only happen when the accumulate(...) method is called,
         * and therefore the initialization needs to be properly synchronized.
         */
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    initialize(leftTuple, innerDeclarations);
                    initialized = true;
                }
            }
        }
        return accumulate((ResultContainer_) context, leftTuple, handle, innerDeclarations);
    }

    protected abstract Runnable accumulate(ResultContainer_ context, Tuple leftTuple, InternalFactHandle handle,
            Declaration[] innerDeclarations);

    protected abstract void initialize(Tuple leftTuple, Declaration[] innerDeclarations);

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
    public final Result_ getResult(Object workingMemoryContext, Object context, Tuple leftTuple,
            Declaration[] declarations, ReteEvaluator reteEvaluator) {
        return finisher.apply((ResultContainer_) context);
    }
}
