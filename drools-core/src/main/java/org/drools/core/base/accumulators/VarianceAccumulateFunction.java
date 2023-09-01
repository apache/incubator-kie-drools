package org.drools.core.base.accumulators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * An implementation of an accumulator capable of calculating variance.
 */
public class VarianceAccumulateFunction extends AbstractAccumulateFunction<VarianceAccumulateFunction.VarianceData> {

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    protected static class VarianceData implements Serializable {

        protected int count;
        protected double mean;
        protected double squaredSum;

    }

    @Override
    public VarianceData createContext() {
        return new VarianceData();
    }

    @Override
    public void init(VarianceData data) {
        data.count = 0;
        data.mean = 0;
        data.squaredSum = 0.0;
    }

    @Override
    public void accumulate(VarianceData data, Object value) {
        double x = ((Number) value).doubleValue();
        // Incremental algorithm to calculate variance:
        // https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
        data.count++;
        double lowerDelta = x - data.mean;
        data.mean += lowerDelta / data.count;
        double higherDelta = x - data.mean;
        data.squaredSum += lowerDelta * higherDelta;
    }

    @Override
    public void reverse(VarianceData data, Object value) {
        double x = ((Number) value).doubleValue();

        double higherDelta = x - data.mean;
        //without this if statement mean becomes NaN, and never escapes from there
        data.mean = data.count == 1 ? 0:data.mean * data.count / (data.count - 1.0) - x / (data.count -1.0);
        double lowerDelta = x - data.mean;
        data.count--;
        data.squaredSum -= lowerDelta * higherDelta;
    }

    @Override
    public Double getResult(VarianceData data) {
        return data.squaredSum / data.count;
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Class<?> getResultType() {
        return Double.class;
    }

}
