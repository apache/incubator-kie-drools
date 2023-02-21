package org.optaplanner.constraint.streams.bavet.common;

import java.util.Objects;

abstract class AbstractGroup<OutTuple_ extends Tuple, GroupKey_, ResultContainer_> {

    public final GroupKey_ groupKey;
    public final OutTuple_ outTuple;
    public int parentCount = 1;

    public AbstractGroup(GroupKey_ groupKey, OutTuple_ outTuple) {
        this.groupKey = groupKey;
        this.outTuple = outTuple;
    }

    public final GroupKey_ getGroupKey() {
        return groupKey;
    }

    public abstract ResultContainer_ getResultContainer();

    public final OutTuple_ getOutTuple() {
        return outTuple;
    }

    @Override
    public final String toString() {
        return Objects.toString(groupKey);
    }

}
