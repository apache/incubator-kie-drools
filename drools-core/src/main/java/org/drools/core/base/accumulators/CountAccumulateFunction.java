package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An implementation of an accumulator capable of counting occurences
 */
public class CountAccumulateFunction extends AbstractAccumulateFunction<CountAccumulateFunction.CountData> {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    protected static class CountData implements Externalizable {
        public long   count = 0;

        public CountData() {}
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            count   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeLong(count);
        }

        @Override
        public String toString() {
            return "count";
        }
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#createContext()
     */
    public CountData createContext() {
        return new CountData();
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(CountData data) {
        data.count = 0;
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#accumulate(java.lang.Object, java.lang.Object)
     */
    public void accumulate(CountData data,
                           Object value) {
        data.count++;
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#reverse(java.lang.Object, java.lang.Object)
     */
    public void reverse(CountData data,
                        Object value) {
        data.count--;
    }

    /* (non-Javadoc)
     * @see org.kie.base.accumulators.AccumulateFunction#getResult(java.lang.Object)
     */
    public Object getResult(CountData data) {
        return data.count;
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
        return Long.class;
    }
}
