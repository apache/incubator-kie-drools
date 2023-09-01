package org.kie.pmml.models.clustering.model;

import org.kie.pmml.api.enums.Named;

public enum KiePMMLCompareFunction implements Named {
    ABS_DIFF("absDiff"),
    GAUSS_SIM("gaussSim"),
    DELTA("delta"),
    EQUAL("equal"),
    TABLE("table");

    private final String name;

    KiePMMLCompareFunction(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public double apply(KiePMMLClusteringField field, double x, double y) {
        switch (this) {
            case ABS_DIFF:
                return absDiff(x, y);
            case GAUSS_SIM:
                return gaussSim(x, y, field.getSimilarityScale().orElse(1.0));
            case DELTA:
                return delta(x, y);
            case EQUAL:
                return equal(x, y);
            case TABLE:
                throw new UnsupportedOperationException("\"table\" compare function not implemented");
        }
        throw new IllegalStateException("Unknown compare function: " + this);
    }

    static double absDiff(double x, double y) {
        return Math.abs(x - y);
    }

    static double gaussSim(double x, double y, double similarityScale) {
        return Math.exp(NEGATIVE_LN_2 * Math.pow(x - y, 2.0) / Math.pow(similarityScale, 2.0));
    }

    static double delta(double x, double y) {
        return doubleEquals(x, y) ? 0.0 : 1.0;
    }

    static double equal(double x, double y) {
        return doubleEquals(x, y) ? 1.0 : 0.0;
    }

    private static boolean doubleEquals(double x, double y) {
        return Double.compare(x, y) == 0;
    }

    private static final double NEGATIVE_LN_2 = -1.0 * Math.log(2.0);

}
