package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class IntegerSumAccumulateFunction extends AbstractAccumulateFunction<IntegerSumAccumulateFunction.SumData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { }

    public void writeExternal(ObjectOutput out) throws IOException { }

    protected static class SumData implements Externalizable {
        public int total = 0;

        public SumData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            total = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(total);
        }
    }

    public SumData createContext() {
        return new SumData();
    }

    public void init(SumData data) {
        data.total = 0;
    }

    public void accumulate(SumData data, Object value) {
        if (value != null) {
            data.total += ( (Integer) value );
        }
    }

    public void reverse(SumData data, Object value) {
        if (value != null) {
            data.total -= ( (Integer) value );
        }
    }

    public Object getResult(SumData data) {
        return data.total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return Integer.class;
    }
}
