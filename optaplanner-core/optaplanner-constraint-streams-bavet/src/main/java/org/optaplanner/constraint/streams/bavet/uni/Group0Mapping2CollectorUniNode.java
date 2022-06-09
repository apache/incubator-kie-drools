package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group0Mapping2CollectorUniNode<OldA, A, B, ResultContainerA_, ResultContainerB_>
        extends AbstractGroupUniNode<OldA, BiTuple<A, B>, String, Object, Pair<A, B>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping2CollectorUniNode(int groupStoreIndex,
            UniConstraintCollector<OldA, ResultContainerA_, A> collectorA,
            UniConstraintCollector<OldA, ResultContainerB_, B> collectorB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB), nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, A, B, ResultContainerA_, ResultContainerB_>
            UniConstraintCollector<OldA, Object, Pair<A, B>> mergeCollectors(
                    UniConstraintCollector<OldA, ResultContainerA_, A> collectorA,
                    UniConstraintCollector<OldA, ResultContainerB_, B> collectorB) {
        return (UniConstraintCollector<OldA, Object, Pair<A, B>>) ConstraintCollectors.compose(collectorA, collectorB,
                Pair::of);
    }

    @Override
    protected String createGroupKey(UniTuple<OldA> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected BiTuple<A, B> createOutTuple(String groupKey) {
        return new BiTuple<>(null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(BiTuple<A, B> outTuple, Pair<A, B> result) {
        outTuple.factA = result.getKey();
        outTuple.factB = result.getValue();
    }

    @Override
    public String toString() {
        return "GroupUniNode 0+2";
    }

}
