package org.drools.impact.analysis.graph;

public enum LinkFilter {

    POSITIVE(true, false, false),
    POSITIVE_NEGATIVE(true, true, false),
    POSITIVE_UNKNOWN(true, false, true),
    NEGATIVE(false, true, false),
    NEGATIVE_UNKNOWN(false, true, true),
    UNKNOWN(false, false, true),
    ALL(true, true, true);

    private final boolean viewPositive;
    private final boolean viewNegative;
    private final boolean viewUnknown;

    private LinkFilter(boolean viewPositive, boolean viewNegative, boolean viewUnknown) {
        this.viewPositive = viewPositive;
        this.viewNegative = viewNegative;
        this.viewUnknown = viewUnknown;
    }

    public boolean isViewPositive() {
        return viewPositive;
    }

    public boolean isViewNegative() {
        return viewNegative;
    }

    public boolean isViewUnknown() {
        return viewUnknown;
    }

    boolean accept(ReactivityType type) {
        return (viewPositive && type == ReactivityType.POSITIVE) ||
               (viewNegative && type == ReactivityType.NEGATIVE) ||
               (viewUnknown && type == ReactivityType.UNKNOWN);
    }
}
