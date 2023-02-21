package org.optaplanner.constraint.streams.bavet.common;

final class GroupWithoutAccumulate<OutTuple_ extends Tuple, GroupKey_, ResultContainer_>
        extends AbstractGroup<OutTuple_, GroupKey_, ResultContainer_> {

    public GroupWithoutAccumulate(GroupKey_ groupKey, OutTuple_ outTuple) {
        super(groupKey, outTuple);
    }

    @Override
    public ResultContainer_ getResultContainer() {
        throw new UnsupportedOperationException();
    }

}
