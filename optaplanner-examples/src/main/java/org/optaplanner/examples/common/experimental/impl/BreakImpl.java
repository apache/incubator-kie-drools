package org.optaplanner.examples.common.experimental.impl;

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.Sequence;

final class BreakImpl<Value_, Difference_ extends Comparable<Difference_>>
        implements Break<Value_, Difference_> {
    private Sequence<Value_, Difference_> previousSequence;
    private Sequence<Value_, Difference_> nextSequence;
    private Difference_ length;

    BreakImpl(Sequence<Value_, Difference_> previousSequence, Sequence<Value_, Difference_> nextSequence,
            Difference_ length) {
        this.previousSequence = previousSequence;
        this.nextSequence = nextSequence;
        this.length = length;
    }

    @Override
    public Sequence<Value_, Difference_> getPreviousSequence() {
        return previousSequence;
    }

    @Override
    public Sequence<Value_, Difference_> getNextSequence() {
        return nextSequence;
    }

    @Override
    public Difference_ getLength() {
        return length;
    }

    void setPreviousSequence(Sequence<Value_, Difference_> previousSequence) {
        this.previousSequence = previousSequence;
    }

    void setNextSequence(Sequence<Value_, Difference_> nextSequence) {
        this.nextSequence = nextSequence;
    }

    void setLength(Difference_ length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Break{" +
                "previousSequence=" + previousSequence +
                ", nextSequence=" + nextSequence +
                ", length=" + length +
                '}';
    }
}
