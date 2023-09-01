package org.drools.impact.analysis.graph;

public enum ReactivityType {
    POSITIVE,
    NEGATIVE,
    UNKNOWN;

    public ReactivityType negate() {
        switch (this) {
            case POSITIVE: return NEGATIVE;
            case NEGATIVE: return POSITIVE;
        }
        return UNKNOWN;
    }

    public ReactivityType combine(ReactivityType type) {
        return this == type ? this : UNKNOWN;
    }

    public static ReactivityType decode(boolean positive) {
        return positive ? POSITIVE : NEGATIVE;
    }
}
