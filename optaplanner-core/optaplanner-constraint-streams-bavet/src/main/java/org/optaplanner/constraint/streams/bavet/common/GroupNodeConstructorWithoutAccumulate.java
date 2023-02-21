package org.optaplanner.constraint.streams.bavet.common;

import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class GroupNodeConstructorWithoutAccumulate<Tuple_ extends Tuple> implements GroupNodeConstructor<Tuple_> {

    private final NodeConstructorWithoutAccumulate<Tuple_> nodeConstructorFunction;

    public GroupNodeConstructorWithoutAccumulate(NodeConstructorWithoutAccumulate<Tuple_> nodeConstructorFunction) {
        this.nodeConstructorFunction = nodeConstructorFunction;
    }

    @Override
    public <Score_ extends Score<Score_>> void build(NodeBuildHelper<Score_> buildHelper, ConstraintStream parentTupleSource,
            ConstraintStream groupStream, List<? extends ConstraintStream> groupStreamChildList, ConstraintStream bridgeStream,
            List<? extends ConstraintStream> bridgeStreamChildList) {
        if (!bridgeStreamChildList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + bridgeStream
                    + ") has an non-empty childStreamList (" + bridgeStreamChildList + ") but it's a groupBy bridge.");
        }
        int groupStoreIndex = buildHelper.reserveTupleStoreIndex(parentTupleSource);
        TupleLifecycle<Tuple_> tupleLifecycle = buildHelper.getAggregatedTupleLifecycle(groupStreamChildList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(groupStream);
        var node = nodeConstructorFunction.apply(groupStoreIndex, tupleLifecycle, outputStoreSize);
        buildHelper.addNode(node, bridgeStream);
    }
}
