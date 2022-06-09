package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class CompositeCountableValueRange<T> extends AbstractCountableValueRange<T> {

    private final List<? extends CountableValueRange<T>> childValueRangeList;
    private final long size;

    public CompositeCountableValueRange(List<? extends CountableValueRange<T>> childValueRangeList) {
        this.childValueRangeList = childValueRangeList;
        long size = 0L;
        for (CountableValueRange<T> childValueRange : childValueRangeList) {
            size += childValueRange.getSize();
        }
        this.size = size;
    }

    public List<? extends ValueRange<T>> getChildValueRangeList() {
        return childValueRangeList;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public T get(long index) {
        long remainingIndex = index;
        for (CountableValueRange<T> childValueRange : childValueRangeList) {
            long childSize = childValueRange.getSize();
            if (remainingIndex < childSize) {
                return childValueRange.get(remainingIndex);
            }
            remainingIndex -= childSize;
        }
        throw new IndexOutOfBoundsException("The index (" + index + ") must be less than the size (" + size + ").");
    }

    @Override
    public boolean contains(T value) {
        for (CountableValueRange<T> childValueRange : childValueRangeList) {
            if (childValueRange.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> createOriginalIterator() {
        Stream<T> stream = Stream.empty();
        for (CountableValueRange<T> childValueRange : childValueRangeList) {
            stream = Stream.concat(stream, originalIteratorToStream(childValueRange));
        }
        return stream.iterator();
    }

    private static <T> Stream<T> originalIteratorToStream(CountableValueRange<T> valueRange) {
        return StreamSupport.stream(
                Spliterators.spliterator(valueRange.createOriginalIterator(), valueRange.getSize(), Spliterator.ORDERED),
                false);
    }

    @Override
    public Iterator<T> createRandomIterator(Random workingRandom) {
        return new RandomCompositeValueRangeIterator(workingRandom);
    }

    private class RandomCompositeValueRangeIterator extends ValueRangeIterator<T> {

        private final Random workingRandom;

        public RandomCompositeValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            long index = RandomUtils.nextLong(workingRandom, size);
            long remainingIndex = index;
            for (CountableValueRange<T> childValueRange : childValueRangeList) {
                long childSize = childValueRange.getSize();
                if (remainingIndex < childSize) {
                    return childValueRange.get(remainingIndex);
                }
                remainingIndex -= childSize;
            }
            throw new IllegalStateException("Impossible state because index (" + index
                    + ") is always less than the size (" + size + ").");
        }

    }

}
