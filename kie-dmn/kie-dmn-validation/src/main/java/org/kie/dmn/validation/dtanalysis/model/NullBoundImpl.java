package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.runtime.Range;

/**
 * For internal use. A semantically null Bound, that is meant NOT to be used for comparison.
 */
@SuppressWarnings("rawtypes")
public class NullBoundImpl extends Bound {

    /**
     * For internal use. A singleton instance of this semantically null Bound, that is meant NOT to be used for comparison in DT gap analysis
     */
    public static final NullBoundImpl NULL = new NullBoundImpl();

    @SuppressWarnings("unchecked")
    private NullBoundImpl() {
        super(null, null, null);
    }

    @Override
    public int compareTo(Bound o) {
        throw new IllegalStateException();
    }

    @Override
    public Comparable getValue() {
        throw new IllegalStateException();
    }

    @Override
    public Range.RangeBoundary getBoundaryType() {
        throw new IllegalStateException();
    }

    @Override
    public Interval getParent() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isLowerBound() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isUpperBound() {
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "NullBoundImpl []";
    }

}
