package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;

abstract class AbstractGroupUniNode<OldA, OutTuple_ extends Tuple, MutableOutTuple_ extends OutTuple_, GroupKey_, ResultContainer_, Result_>
        extends AbstractGroupNode<UniTuple<OldA>, OutTuple_, MutableOutTuple_, GroupKey_, ResultContainer_, Result_> {

    private final BiFunction<ResultContainer_, OldA, Runnable> accumulator;

    protected AbstractGroupUniNode(int groupStoreIndex, int undoStoreIndex,
            Function<UniTuple<OldA>, GroupKey_> groupKeyFunction,
            UniConstraintCollector<OldA, ResultContainer_, Result_> collector,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, EnvironmentMode environmentMode) {
        super(groupStoreIndex, undoStoreIndex, groupKeyFunction,
                collector == null ? null : collector.supplier(),
                collector == null ? null : collector.finisher(),
                nextNodesTupleLifecycle, environmentMode);
        accumulator = collector == null ? null : collector.accumulator();
    }

    protected AbstractGroupUniNode(int groupStoreIndex, Function<UniTuple<OldA>, GroupKey_> groupKeyFunction,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle, EnvironmentMode environmentMode) {
        super(groupStoreIndex, groupKeyFunction, nextNodesTupleLifecycle, environmentMode);
        accumulator = null;
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, UniTuple<OldA> tuple) {
        return accumulator.apply(resultContainer, tuple.getFactA());
    }
}
