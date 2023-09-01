package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An implementation of an accumulator capable of calculating sum of values
 */
public class SumAccumulateFunction extends AbstractAccumulateFunction<SumAccumulateFunction.SumData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { }

    public void writeExternal(ObjectOutput out) throws IOException { }

    protected static class SumData implements Externalizable {
        public double total = 0;

        public SumData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            total   = in.readDouble();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeDouble(total);
        }

        @Override
        public String toString() {
            return "sum";
        }
    }

    public SumData createContext() {
        return new SumData();
    }

    public void init(SumData data) {
        data.total = 0;
    }

    public void accumulate(SumData data,
                           Object value) {
        data.total += ((Number) value).doubleValue();
    }

    public void reverse(SumData data,
                        Object value) {
        data.total -= ((Number) value).doubleValue();
    }

    public Object getResult(SumData data) {
        return data.total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return Double.class;
    }
}
