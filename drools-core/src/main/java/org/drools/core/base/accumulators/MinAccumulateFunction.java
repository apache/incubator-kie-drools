/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
