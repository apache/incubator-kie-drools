package org.kie.pmml.models.clustering.model.compare;

import java.math.BigDecimal;

public class CompareFunctions {

    private static final double NEGATIVE_LN_2 = -1.0 * Math.log(2.0);

    private static final CompareFunction ABS_DIFF = (x,y) -> Math.abs(x - y);
    private static final CompareFunction DELTA = (x,y) -> doubleEquals(x, y) ? 0.0 : 1.0;
    private static final CompareFunction EQUAL = (x,y) -> doubleEquals(x, y) ? 1.0 : 0.0;

    public static CompareFunction absDiff() {
        return ABS_DIFF;
    }

    public static CompareFunction gaussSim(double similarityScale) {
        return (x,y) -> Math.exp(NEGATIVE_LN_2 * Math.pow(x - y, 2.0) / Math.pow(similarityScale, 2.0));
    }

    public static CompareFunction delta(double x, double y) {
        return DELTA;
    }

    public static CompareFunction equal(double x, double y) {
        return EQUAL;
    }

    public static CompareFunction table(double x, double y) {
        throw new UnsupportedOperationException("\"table\" compare function not implemented");
    }

    private static boolean doubleEquals(double x, double y) {
        return BigDecimal.valueOf(x).compareTo(BigDecimal.valueOf(y)) == 0;
    }

    private CompareFunctions() {
        // not allowed for util class
    }

}
