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
 * An implementation of an accumulator capable of calculating maximum values
 */
public class MaxAccumulateFunction extends AbstractAccumulateFunction {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    protected static class MaxData implements Externalizable {
        public Comparable max = null;

        public MaxData() {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            max = (Comparable) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(max);
        }
    }

    public Serializable createContext() {
        return new MaxData();
    }

    public void init(Serializable context) {
        MaxData data = (MaxData) context;
        data.max = null;
    }

    public void accumulate(Serializable context,
                           Object value) {
        if (value != null) {
            MaxData data = (MaxData) context;
            data.max = data.max == null || data.max.compareTo( value ) < 0 ?
                       (Comparable) value :
                       data.max;
        }
    }

    public void reverse(Serializable context,
                        Object value) {
    }

    public Object getResult(Serializable context) {
        MaxData data = (MaxData) context;
        return data.max;
    }

    public boolean supportsReverse() {
        return false;
    }

    public Class<?> getResultType() {
        return Comparable.class;
    }
}
