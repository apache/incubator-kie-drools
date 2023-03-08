package org.optaplanner.constraint.streams.bavet.common;

import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class GroupNodeConstructorWithAccumulate<Tuple_ extends Tuple> implements GroupNodeConstructor<Tuple_> {

    private final NodeConstructorWithAccumulate<Tuple_> nodeConstructorFunction;

    public GroupNodeConstructorWithAccumulate(NodeConstructorWithAccumulate<Tuple_> nodeConstructorFunction) {
        this.nodeConstructorFunction = nodeConstructorFunction;
    }

    @Override
    public <Solution_, Score_ extends Score<Score_>> void build(NodeBuildHelper<Score_> buildHelper,
            BavetAbstractConstraintStream<Solution_> parentTupleSource,
            BavetAbstractConstraintStream<Solution_> groupStream, List<? extends ConstraintStream> groupStreamChildList,
            BavetAbstractConstraintStream<Solution_> bridgeStream, List<? extends ConstraintStream> bridgeStreamChildList,
            EnvironmentMode environmentMode) {
        if (!bridgeStreamChildList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + bridgeStream
                    + ") has an non-empty childStreamList (" + bridgeStreamChildList + ") but it's a groupBy bridge.");
        }
        int groupStoreIndex = buildHelper.reserveTupleStoreIndex(parentTupleSource);
        int undoStoreIndex = buildHelper.reserveTupleStoreIndex(parentTupleSource);
        TupleLifecycle<Tuple_> tupleLifecycle = buildHelper.getAggregatedTupleLifecycle(groupStreamChildList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(groupStream);
        var node = nodeConstructorFunction.apply(groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize,
                environmentMode);
        buildHelper.addNode(node, bridgeStream);
    }
}
