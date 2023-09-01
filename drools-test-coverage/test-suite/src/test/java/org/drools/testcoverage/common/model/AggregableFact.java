package org.drools.testcoverage.common.model;

import java.io.Serializable;

/**
 * Simple fact class used in aggregation test. Holds one value that can be
 * aggregated.
 */
public class AggregableFact implements Serializable {

    private static final long serialVersionUID = 3491698741720234098L;
    private double value;

    public AggregableFact() {
        value = 0.0;
    }

    public AggregableFact(final double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(final double value) {
        this.value = value;
    }
}
