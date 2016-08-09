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

import org.kie.api.runtime.rule.AccumulateFunction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class LongSumAccumulateFunction implements AccumulateFunction {

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

    public Serializable createContext() {
        return new SumData();
    }

    public void init(Serializable context) {
        ((SumData) context).total = 0;
    }

    public void accumulate(Serializable context, Object value) {
        if (value != null) {
            ( (SumData) context ).total += ( (Long) value );
        }
    }

    public void reverse(Serializable context, Object value) {
        if (value != null) {
            ( (SumData) context ).total -= ( (Long) value );
        }
    }

    public Object getResult(Serializable context) {
        return ((SumData) context).total;
    }

    public boolean supportsReverse() {
        return true;
    }

    public Class<?> getResultType() {
        return Long.class;
    }
}
