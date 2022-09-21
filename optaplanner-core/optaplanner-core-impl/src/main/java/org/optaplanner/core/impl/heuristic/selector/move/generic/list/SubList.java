package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;

public final class SubList {

    private final Object entity;
    private final int fromIndex;
    private final int length;

    SubList(Object entity, int fromIndex, int length) {
        this.entity = entity;
        this.fromIndex = fromIndex;
        this.length = length;
    }

    public Object getEntity() {
        return entity;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getLength() {
        return length;
    }

    public int getToIndex() {
        return fromIndex + length;
    }

    public SubList rebase(ScoreDirector<?> destinationScoreDirector) {
        return new SubList(destinationScoreDirector.lookUpWorkingObject(entity), fromIndex, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubList other = (SubList) o;
        return fromIndex == other.fromIndex && length == other.length && entity.equals(other.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, fromIndex, length);
    }

    @Override
    public String toString() {
        return entity + "[" + fromIndex + ".." + getToIndex() + "]";
    }
}
