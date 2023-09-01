package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;

/**
 * An implementation of an accumulator capable of calculating sum of values
 */
public class BigIntegerSumAccumulateFunction extends AbstractAccumulateFunction<BigIntegerSumAccumulateFunction.SumData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { }

    public void writeExternal(ObjectOutput out) throws IOException { }

    protected static class SumData implements Externalizable {
        public BigInteger total = BigInteger.ZERO;

        public SumData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            total = (BigInteger) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( total );
        }
    }

    public SumData createContext() {
        return new SumData();
    }

    public void init(SumData data) {
        data.total = BigInteger.ZERO;
    }

    public void accumulate(SumData data,
                           Object value) {
        if (value != null) {
            data.total = data.total.add( (BigInteger) value );
        }
    }

    public void reverse(SumData data,
                        Object value) {
        if (value != null) {
            data.total = data.total.subtract( (BigInteger) value );
        }
    }

    public Object getResult(SumData data) {
        return data.total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return BigInteger.class;
    }
}
