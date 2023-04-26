/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.functions;

import java.util.function.Function;

import io.serverlessworkflow.api.functions.FunctionDefinition;

public class FunctionDefinitionEx<T, V> extends FunctionDefinition {

    private static final long serialVersionUID = 1L;
    private transient Function<T, V> function;

    public FunctionDefinitionEx(String name) {
        super(name);
    }

    public FunctionDefinition withFunction(Function<T, V> function) {
        this.function = function;
        return this;
    }

    public Function<T, V> getFunction() {
        return function;
    }
}
