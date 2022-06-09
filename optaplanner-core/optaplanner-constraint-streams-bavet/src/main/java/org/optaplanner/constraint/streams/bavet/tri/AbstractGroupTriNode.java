package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

abstract class AbstractGroupTriNode<OldA, OldB, OldC, OutTuple_ extends Tuple, GroupKey_, ResultContainer_, Result_>
        extends AbstractGroupNode<TriTuple<OldA, OldB, OldC>, OutTuple_, GroupKey_, ResultContainer_, Result_> {

    private final QuadFunction<ResultContainer_, OldA, OldB, OldC, Runnable> accumulator;

    protected AbstractGroupTriNode(int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainer_, Result_> collector,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        super(groupStoreIndex,
                collector == null ? null : collector.supplier(),
                collector == null ? null : collector.finisher(),
                nextNodesTupleLifecycle);
        accumulator = collector == null ? null : collector.accumulator();
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, TriTuple<OldA, OldB, OldC> tuple) {
        return accumulator.apply(resultContainer, tuple.factA, tuple.factB, tuple.factC);
    }

}
