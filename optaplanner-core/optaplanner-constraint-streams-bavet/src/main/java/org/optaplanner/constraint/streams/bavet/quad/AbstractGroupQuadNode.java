package org.optaplanner.constraint.streams.bavet.quad;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

abstract class AbstractGroupQuadNode<OldA, OldB, OldC, OldD, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_, GroupKey_, ResultContainer_, Result_>
        extends
        AbstractGroupNode<QuadTuple<OldA, OldB, OldC, OldD>, OutTuple_, MutableOutTuple_, GroupKey_, ResultContainer_, Result_> {

    private final PentaFunction<ResultContainer_, OldA, OldB, OldC, OldD, Runnable> accumulator;

    protected AbstractGroupQuadNode(int groupStoreIndex, int undoStoreIndex,
            Function<QuadTuple<OldA, OldB, OldC, OldD>, GroupKey_> groupKeyFunction,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, Result_> collector,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, EnvironmentMode environmentMode) {
        super(groupStoreIndex, undoStoreIndex, groupKeyFunction,
                collector == null ? null : collector.supplier(),
                collector == null ? null : collector.finisher(),
                nextNodesTupleLifecycle, environmentMode);
        accumulator = collector == null ? null : collector.accumulator();
    }

    protected AbstractGroupQuadNode(int groupStoreIndex,
            Function<QuadTuple<OldA, OldB, OldC, OldD>, GroupKey_> groupKeyFunction,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, EnvironmentMode environmentMode) {
        super(groupStoreIndex, groupKeyFunction, nextNodesTupleLifecycle, environmentMode);
        accumulator = null;
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return accumulator.apply(resultContainer, tuple.getFactA(), tuple.getFactB(), tuple.getFactC(), tuple.getFactD());
    }

}
