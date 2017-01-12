/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * An implementation of an accumulator capable of calculating sum of values
 */
public class BigIntegerSumAccumulateFunction extends AbstractAccumulateFunction {

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

    public Serializable createContext() {
        return new SumData();
    }

    public void init(Serializable context) {
        SumData data = (SumData) context;
        data.total = BigInteger.ZERO;
    }

    public void accumulate(Serializable context,
                           Object value) {
        if (value != null) {
            SumData data = (SumData) context;
            data.total = data.total.add( (BigInteger) value );
        }
    }

    public void reverse(Serializable context,
                        Object value) {
        if (value != null) {
            SumData data = (SumData) context;
            data.total = data.total.subtract( (BigInteger) value );
        }
    }

    public Object getResult(Serializable context) {
        SumData data = (SumData) context;
        return data.total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return BigInteger.class;
    }
}
