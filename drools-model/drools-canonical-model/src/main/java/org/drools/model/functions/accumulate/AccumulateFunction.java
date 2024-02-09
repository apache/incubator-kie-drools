/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.functions.accumulate;

import java.util.function.Supplier;

import org.drools.model.Argument;
import org.drools.model.Value;
import org.drools.model.Variable;

public class AccumulateFunction {
    private Variable result;
    private Variable[] externalVars;

    protected final Argument source;
    protected final Supplier<?> functionSupplier;

    public AccumulateFunction(Argument source, Supplier<?> functionSupplier) {
        this.source = source;
        this.functionSupplier = functionSupplier;
    }

    public Argument getSource() {
        return source;
    }

    public Object createFunctionObject() {
        return functionSupplier.get();
    }

    public Variable getResult() {
        return result;
    }

    public AccumulateFunction as(Variable result) {
        this.result = result;
        return this;
    }

    public Variable[] getExternalVars() {
        return externalVars;
    }

    public AccumulateFunction with(Variable... externalVars) {
        this.externalVars = externalVars;
        return this;
    }

    public boolean isFixedValue() {
        return source instanceof Value;
    }
}
