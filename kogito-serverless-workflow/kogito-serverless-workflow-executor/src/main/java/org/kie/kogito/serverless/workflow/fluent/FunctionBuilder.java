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
package org.kie.kogito.serverless.workflow.fluent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.serverless.workflow.parser.types.JavaTypeHandler;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class FunctionBuilder {

    private FunctionDefinition functionDefinition;

    public static enum HttpMethod {
        post,
        get,
        put,
        patch,
        delete,
        head,
        trace,
        options,
        connect
    };

    public static FunctionBuilder def(String name, FunctionDefinition.Type type, String operation) {
        return new FunctionBuilder(new FunctionDefinition(name).withType(type).withOperation(operation));
    }

    public static FunctionBuilder rest(String name, HttpMethod method, String uri) {
        return new FunctionBuilder(new FunctionDefinition(name).withType(Type.CUSTOM).withOperation("rest:" + method + ":" + uri));
    }

    public static FunctionBuilder expr(String name, String expression) {
        return new FunctionBuilder(new FunctionDefinition(name).withType(Type.EXPRESSION).withOperation(expression));
    }

    public static <T> FunctionBuilder java(String name, Function<T, ?> function) {
        return new FunctionBuilder(new FunctionDefinitionEx(name).withFunction(function).withType(Type.CUSTOM).withOperation(JavaTypeHandler.JAVA_TYPE));
    }

    private FunctionBuilder(FunctionDefinition functionDefinition) {
        this.functionDefinition = functionDefinition;
    }

    public FunctionBuilder metadata(String key, String value) {
        Map<String, String> metadata = functionDefinition.getMetadata();

        if (metadata == null) {
            metadata = new HashMap<>();
            functionDefinition.withMetadata(metadata);
        }
        metadata.put(key, value);
        return this;
    }

    public FunctionDefinition build() {
        return functionDefinition;
    }

}
