/*
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
package org.kie.kogito.serverless.workflow.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.serverlessworkflow.api.functions.FunctionDefinition;

public class FunctionDefinitionEx extends FunctionDefinition {

    private static final long serialVersionUID = 1L;
    private transient Function function;
    private transient BiFunction bifunction;
    private transient TriFunction trifunction;

    public FunctionDefinitionEx(String name) {
        super(name);
    }

    public FunctionDefinition withFunction(Function function) {
        this.function = function;
        return this;
    }

    public FunctionDefinition withBiFunction(BiFunction function) {
        this.bifunction = function;
        return this;
    }

    public FunctionDefinition withTriFunction(TriFunction function) {
        this.trifunction = function;
        return this;
    }

    public Function getFunction() {
        return function;
    }

    public BiFunction getBiFunction() {
        return bifunction;
    }

    public TriFunction getTriFunction() {
        return trifunction;
    }
}
