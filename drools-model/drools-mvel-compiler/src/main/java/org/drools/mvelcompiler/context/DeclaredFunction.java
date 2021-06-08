/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvelcompiler.context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.core.addon.TypeResolver;
import org.drools.core.util.StreamUtils;

public class DeclaredFunction {

    private final TypeResolver typeResolver;
    private final String name;
    private final String returnType;
    private final List<String> arguments;

    public DeclaredFunction(TypeResolver typeResolver, String name, String returnType, List<String> arguments) {
        this.typeResolver = typeResolver;
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public Optional<Class<?>> findReturnType() {
        return resolveType(returnType);
    }

    public List<Class<?>> findArgumentsType() {
        return arguments.stream().map(this::resolveType)
                .flatMap(StreamUtils::optionalToStream)
                .collect(Collectors.toList());
    }

    public Optional<Class<?>> resolveType(String name) {
        try {
            return Optional.ofNullable(typeResolver.resolveType(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
