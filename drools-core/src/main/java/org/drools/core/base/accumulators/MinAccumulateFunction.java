package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An implementation of an accumulator capable of calculating minimun values
 */
public class MinAccumulateFunction extends AbstractAccumulateFunction<MinAccumulateFunction.MinData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    protected static class MinData implements Externalizable {
        public Comparable min = null;
        
        public MinData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            min = (Comparable) in.readObject();
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

    public void init(MinData data) {
        data.min = null;
    }

    public void accumulate(MinData data,
                           Object value) {
        if (value != null) {
            data.min = data.min == null || data.min.compareTo( value ) > 0 ?
                       (Comparable) value :
                       data.min;
        }
    }

    @Override
    public boolean tryReverse( MinData data, Object value ) {
        if (value != null) {
            return data.min.compareTo( value ) < 0;
        }
        return true;
    }

    public void reverse(MinData data,
                        Object value) {
    }

    public Object getResult(MinData data) {
        return data.min;
    }

    public boolean supportsReverse() {
        return false;
    }

    public Class<?> getResultType() {
        return Comparable.class;
    }
}
