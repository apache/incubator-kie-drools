package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An implementation of an accumulator capable of calculating average values
 */
public class AverageAccumulateFunction extends AbstractAccumulateFunction<AverageAccumulateFunction.AverageData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public static class AverageData implements Externalizable {
        public int    count = 0;
        public double total = 0;
        
        public AverageData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            count   = in.readInt();
            total   = in.readDouble();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(count);
            out.writeDouble(total);
        }

    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#createContext()
     */
    public AverageData createContext() {
        return new AverageData();
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(AverageData data) {
        data.count = 0;
        data.total = 0;
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#accumulate(java.lang.Object, java.lang.Object)
     */
    public void accumulate(AverageData data,
                           Object value) {
        if (value != null) {
            data.count++;
            data.total += ( (Number) value ).doubleValue();
        }
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#reverse(java.lang.Object, java.lang.Object)
     */
    public void reverse(AverageData data,
                        Object value) {
        if (value != null) {
            data.count--;
            data.total -= ( (Number) value ).doubleValue();
        }
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#getResult(java.lang.Object)
     */
    public Object getResult(AverageData data) {
        return data.count == 0 ? null : data.total / data.count;
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#supportsReverse()
     */
    public boolean supportsReverse() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Class< ? > getResultType() {
        return Double.class;
    }
}
