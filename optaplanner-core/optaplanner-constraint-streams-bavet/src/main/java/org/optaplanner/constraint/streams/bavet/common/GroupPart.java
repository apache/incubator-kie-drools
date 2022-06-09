package org.optaplanner.constraint.streams.bavet.common;

final class GroupPart<Group_> {

    public final Group_ group;
    public final Runnable undoAccumulator;

    public GroupPart(Group_ group, Runnable undoAccumulator) {
        this.group = group;
        this.undoAccumulator = undoAccumulator;
    }

}
