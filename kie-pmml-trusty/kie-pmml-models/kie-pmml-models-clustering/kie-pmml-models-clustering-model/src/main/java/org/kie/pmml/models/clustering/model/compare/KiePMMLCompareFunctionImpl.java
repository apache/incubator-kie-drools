package org.kie.pmml.models.clustering.model.compare;

import java.math.BigDecimal;

public class KiePMMLCompareFunctionImpl {

    private static final double NEGATIVE_LN_2 = -1.0 * Math.log(2.0);

    private static final KiePMMLCompareFunction.Function ABS_DIFF = (x, y) -> Math.abs(x - y);
    private static final KiePMMLCompareFunction.Function DELTA = (x, y) -> doubleEquals(x, y) ? 0.0 : 1.0;
    private static final KiePMMLCompareFunction.Function EQUAL = (x, y) -> doubleEquals(x, y) ? 1.0 : 0.0;

    public static KiePMMLCompareFunction.Function absDiff() {
        return ABS_DIFF;
    }

    public static KiePMMLCompareFunction.Function gaussSim(double similarityScale) {
        return (x,y) -> Math.exp(NEGATIVE_LN_2 * Math.pow(x - y, 2.0) / Math.pow(similarityScale, 2.0));
    }

    public static KiePMMLCompareFunction.Function delta() {
        return DELTA;
    }

    public static KiePMMLCompareFunction.Function equal() {
        return EQUAL;
    }

    public static KiePMMLCompareFunction.Function table() {
        throw new UnsupportedOperationException("\"table\" compare function not implemented");
    }

    private static boolean doubleEquals(double x, double y) {
        return BigDecimal.valueOf(x).compareTo(BigDecimal.valueOf(y)) == 0;
    }

    private KiePMMLCompareFunctionImpl() {
        // not allowed for util class
    }

}
