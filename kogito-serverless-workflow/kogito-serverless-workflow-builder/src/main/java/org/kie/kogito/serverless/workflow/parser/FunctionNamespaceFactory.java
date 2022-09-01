/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import io.serverlessworkflow.api.functions.FunctionRef;

public class FunctionNamespaceFactory {

    public static FunctionNamespaceFactory instance() {
        return INSTANCE;
    }

    private static final FunctionNamespaceFactory INSTANCE = new FunctionNamespaceFactory();
    private static final String NAMESPACE_SEPARATOR = ":";

    private final Map<String, FunctionNamespace> namespaceMap = new HashMap<>();

    public Optional<FunctionNamespace> getNamespace(FunctionRef functionDef) {
        return Optional.ofNullable(namespaceMap.get(extractNamespace(functionDef)));
    }

    private static String extractNamespace(FunctionRef functionRef) {
        String name = functionRef.getRefName();
        int indexOf = name.lastIndexOf(NAMESPACE_SEPARATOR);
        return indexOf == -1 ? name : name.substring(0, indexOf);
    }

    public static String getFunctionName(FunctionRef functionRef) {
        String name = functionRef.getRefName();
        int indexOf = name.lastIndexOf(NAMESPACE_SEPARATOR);
        return indexOf == -1 ? name : name.substring(indexOf + 1);
    }

    private FunctionNamespaceFactory() {
        ServiceLoader.load(FunctionNamespace.class).forEach(handler -> namespaceMap.put(handler.namespace(), handler));
    }
}
