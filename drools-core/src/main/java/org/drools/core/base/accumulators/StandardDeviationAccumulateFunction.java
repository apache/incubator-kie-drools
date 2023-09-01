package org.drools.core.base.accumulators;


/**
 * An implementation of an accumulator capable of calculating standard deviation.
 */
public class StandardDeviationAccumulateFunction extends VarianceAccumulateFunction {

    @Override
    public Double getResult(VarianceData data) {
        return Math.sqrt(super.getResult(data));
    }

}
