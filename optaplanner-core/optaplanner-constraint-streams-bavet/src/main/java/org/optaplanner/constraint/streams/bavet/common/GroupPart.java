package org.optaplanner.constraint.streams.bavet.common;

final class GroupPart<Group_> {

    public final Group_ group;
    private final Runnable undoAccumulator;

    public GroupPart(Group_ group, Runnable undoAccumulator) {
        this.group = group;
        this.undoAccumulator = undoAccumulator;
    }

    void undoAccumulate() {
        if (undoAccumulator != null) {
            undoAccumulator.run();
        }
    }

}
