package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LongSumAccumulateFunction extends AbstractAccumulateFunction<LongSumAccumulateFunction.SumData> {

    public void readExternal(ObjectInput in ) throws IOException, ClassNotFoundException { }

    public void writeExternal(ObjectOutput out ) throws IOException { }

    protected static class SumData implements Externalizable {
        public long total = 0L;

        public SumData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            total = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeLong(total);
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
            data.total += ( (Long) value );
        }
    }

    public void reverse(SumData data, Object value) {
        if (value != null) {
            data.total -= ( (Long) value );
        }
    }

    public Object getResult(SumData data) {
        return data.total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return Long.class;
    }
}
