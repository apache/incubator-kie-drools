package org.optaplanner.constraint.streams.bavet.common;

final class GroupWithoutAccumulate<OutTuple_ extends Tuple, ResultContainer_>
        extends AbstractGroup<OutTuple_, ResultContainer_> {

    public GroupWithoutAccumulate(Object groupKey, OutTuple_ outTuple) {
        super(groupKey, outTuple);
    }

    @Override
    public ResultContainer_ getResultContainer() {
        throw new UnsupportedOperationException();
    }

}
