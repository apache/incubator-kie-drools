package org.optaplanner.examples.common.optional.score;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.kie.api.runtime.rule.AccumulateFunction;

public class LoadBalanceAccumulateFunction implements AccumulateFunction<LoadBalanceAccumulateFunction.LoadBalanceData> {

    protected static class LoadBalanceData implements Serializable {

        private long n;
        private long sum;
        // the sum of squared deviation from zero
        private long squaredSum;

    }

    @Override
    public LoadBalanceData createContext() {
        return new LoadBalanceData();
    }

    @Override
    public void init(LoadBalanceData data) {
        data.n = 0L;
        data.sum = 0L;
        data.squaredSum = 0L;
    }

    @Override
    public void accumulate(LoadBalanceData data, Object o) {
        long value = (long) o;
        data.n++;
        data.sum += value;
        data.squaredSum += value * value;
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public void reverse(LoadBalanceData data, Object o) {
        long value = (long) o;
        data.n--;
        data.sum -= value;
        data.squaredSum -= value * value;
    }

    @Override
    public Class<LoadBalanceResult> getResultType() {
        return LoadBalanceResult.class;
    }

    @Override
    public LoadBalanceResult getResult(LoadBalanceData data) {
        return new LoadBalanceResult(data.n, data.sum, data.squaredSum);
    }

    @Override
    public void writeExternal(ObjectOutput out) {
    }

    @Override
    public void readExternal(ObjectInput in) {
    }

    public static class LoadBalanceResult implements Serializable {

        private final long n;
        private final long sum;
        private final long squaredSum;

        public LoadBalanceResult(long n, long sum, long squaredSum) {
            this.n = n;
            this.sum = sum;
            this.squaredSum = squaredSum;
        }

        public long getMeanDeviationSquaredSumRootMillis() {
            return getMeanDeviationSquaredSumRoot(1_000.0);
        }

        public long getMeanDeviationSquaredSumRootMicros() {
            return getMeanDeviationSquaredSumRoot(1_000_000.0);
        }

        /**
         * Like standard deviation, but doesn't divide by n.
         *
         * @param scaleMultiplier {@code > 0}
         * @return {@code >= 0}, {@code latexmath:[f(n) = \sqrt{\sum_{i=1}^{n} (x_i - \overline{x})^2}]} multiplied by
         *         scaleMultiplier
         */
        public long getMeanDeviationSquaredSumRoot(double scaleMultiplier) {
            // quicklatex.com: f(n) = \sqrt{\sum_{i=1}^{n} (x_i - \overline{x})^2} = \sqrt{\sum_{i=1}^{n} x_i^2 - \frac{(\sum_{i=1}^{n} x_i)^2}{n}}
            double meanDeviationSquaredSum = squaredSum - ((double) (sum * sum) / n);
            return (long) (Math.sqrt(meanDeviationSquaredSum) * scaleMultiplier);
        }
    }

}
