package org.kie.dmn.trisotech.core.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;

public class IterableRange implements Iterable<BigDecimal> {

    private final static BigDecimal STEP = new BigDecimal(1);

    private final Range range;

    public IterableRange(Range range) {
        this.range = range;
    }

    @Override
    public Iterator<BigDecimal> iterator() {
        return new IterableRangeIterator();
    }

    private class IterableRangeIterator implements Iterator<BigDecimal> {

        private BigDecimal current = null;
        private final BigDecimal from = (BigDecimal) range.getLowEndPoint();
        private final BigDecimal to = (BigDecimal) range.getHighEndPoint();

        @Override
        public boolean hasNext() {
            if (current == null) {
                return true;
            }
            if (range.getHighBoundary() == RangeBoundary.CLOSED) {
                return current.add(STEP).compareTo(to) <= 0;
            } else {
                return current.add(STEP).compareTo(to) < 0;
            }
        }

        @Override
        public BigDecimal next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (current == null) {
                if (range.getLowBoundary() == RangeBoundary.CLOSED) {
                    current = from;
                } else {
                    current = from.add(STEP);
                }
            } else {
                current = current.add(STEP);
            }
            return current;
        }

    }

}
