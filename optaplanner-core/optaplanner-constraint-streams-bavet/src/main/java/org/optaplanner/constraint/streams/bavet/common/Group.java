package org.optaplanner.constraint.streams.bavet.common;

import java.util.Objects;

final class Group<OutTuple_ extends Tuple, GroupKey_, ResultContainer_> {

    public final GroupKey_ groupKey;
    public final ResultContainer_ resultContainer;
    public final OutTuple_ outTuple;
    public int parentCount = 0;

    public Group(GroupKey_ groupKey, ResultContainer_ resultContainer, OutTuple_ outTuple) {
        this.groupKey = groupKey;
        this.resultContainer = resultContainer;
        this.outTuple = outTuple;
    }

    @Override
    public String toString() {
        return Objects.toString(groupKey);
    }

}
