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

import io.serverlessworkflow.api.functions.FunctionDefinition;

public class FunctionTypeHandlerFactory {

    public static FunctionTypeHandlerFactory instance() {
        return INSTANCE;
    }

    private static final FunctionTypeHandlerFactory INSTANCE = new FunctionTypeHandlerFactory();
    private static final String CUSTOM_TYPE_SEPARATOR = ":";

    private final Map<String, FunctionTypeHandler> typeMap = new HashMap<>();
    private final Map<String, FunctionTypeHandler> customMap = new HashMap<>();

    public Optional<FunctionTypeHandler> getTypeHandler(FunctionDefinition functionDef) {
        final boolean isCustom = functionDef.getType() == FunctionDefinition.Type.CUSTOM;
        return Optional.ofNullable(getMap(isCustom).get(isCustom ? getTypeFromOperation(functionDef) : functionDef.getType().toString()));
    }

    private static String getTypeFromOperation(FunctionDefinition functionDef) {
        String operation = functionDef.getOperation();
        int indexOf = operation.indexOf(CUSTOM_TYPE_SEPARATOR);
        return indexOf == -1 ? operation : operation.substring(0, indexOf);
    }

    public static String trimCustomOperation(FunctionDefinition functionDef) {
        String operation = functionDef.getOperation();
        int indexOf = operation.indexOf(CUSTOM_TYPE_SEPARATOR);
        return indexOf == -1 ? operation : operation.substring(indexOf + 1);
    }

    private Map<String, FunctionTypeHandler> getMap(boolean isCustom) {
        return isCustom ? customMap : typeMap;
    }

    private FunctionTypeHandlerFactory() {
        ServiceLoader.load(FunctionTypeHandler.class).forEach(handler -> getMap(handler.isCustom()).put(handler.type(), handler));
    }
}
