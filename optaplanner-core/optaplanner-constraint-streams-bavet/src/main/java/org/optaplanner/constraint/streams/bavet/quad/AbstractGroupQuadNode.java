package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

abstract class AbstractGroupQuadNode<OldA, OldB, OldC, OldD, OutTuple_ extends Tuple, GroupKey_, ResultContainer_, Result_>
        extends AbstractGroupNode<QuadTuple<OldA, OldB, OldC, OldD>, OutTuple_, GroupKey_, ResultContainer_, Result_> {

    private final PentaFunction<ResultContainer_, OldA, OldB, OldC, OldD, Runnable> accumulator;

    protected AbstractGroupQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, Result_> collector,
            TupleLifecycle<OutTuple_> nextNodesTupleLifecycle) {
        super(groupStoreIndex,
                collector == null ? null : collector.supplier(),
                collector == null ? null : collector.finisher(),
                nextNodesTupleLifecycle);
        accumulator = collector == null ? null : collector.accumulator();
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return accumulator.apply(resultContainer, tuple.factA, tuple.factB, tuple.factC, tuple.factD);
    }

}
