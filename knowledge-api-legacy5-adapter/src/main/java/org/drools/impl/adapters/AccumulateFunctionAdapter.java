/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.compiler.lang.dsl.DSLMapParser;
import org.kie.api.runtime.rule.AccumulateFunction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class AccumulateFunctionAdapter implements AccumulateFunction {

    private final org.drools.runtime.rule.AccumulateFunction delegate;

    public AccumulateFunctionAdapter(org.drools.runtime.rule.AccumulateFunction delegate) {
        this.delegate = delegate;
    }

    @Override
    public Serializable createContext() {
        return delegate.createContext();
    }

    @Override
    public void init(Serializable context) throws Exception {
        delegate.init(context);
    }

    @Override
    public void accumulate(Serializable context, Object value) {
        delegate.accumulate(context, value);
    }

    @Override
    public void reverse(Serializable context, Object value) throws Exception {
        delegate.reverse(context, value);
    }

    @Override
    public Object getResult(Serializable context) throws Exception {
        return delegate.getResult(context);
    }

    @Override
    public boolean supportsReverse() {
        return delegate.supportsReverse();
    }

    @Override
    public Class<?> getResultType() {
        return null;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        delegate.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        delegate.readExternal(in);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccumulateFunctionAdapter && delegate.equals(((AccumulateFunctionAdapter)obj).delegate);
    }
}
