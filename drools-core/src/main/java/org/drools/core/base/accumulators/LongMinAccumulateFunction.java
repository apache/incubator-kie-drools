package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An implementation of an accumulator capable of calculating maximum values
 */
public class LongMinAccumulateFunction extends AbstractAccumulateFunction<LongMinAccumulateFunction.MinData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    protected static class MinData implements Externalizable {
        public Long min = null;

        public MinData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            min = (Long) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(min);
        }

        @Override
        public String toString() {
            return "min";
        }
    }

    public MinData createContext() {
        return new MinData();
    }

    public void init( MinData data) {
        data.min = null;
    }

    public void accumulate( MinData data,
                            Object value) {
        if (value != null) {
            Long number = (Long)value;
            data.min = data.min == null || data.min > number ? number : data.min;
        }
    }

    public void reverse( MinData data,
                         Object value) {
    }

    @Override
    public boolean tryReverse( MinData data, Object value ) {
        if (value != null) {
            Long number = (Long)value;
            return data.min < number;
        }
        return true;
    }

    public Object getResult( MinData data) {
        return data.min;
    }

    public boolean supportsReverse() {
        return false;
    }

    public Class<?> getResultType() {
        return Long.class;
    }
}
