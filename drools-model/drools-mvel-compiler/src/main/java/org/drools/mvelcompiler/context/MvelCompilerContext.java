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
package org.drools.mvelcompiler.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.util.TypeResolver;
import org.drools.mvelcompiler.MvelCompilerException;
import org.drools.mvelcompiler.ast.RootTypeThisExpr;
import org.drools.mvelcompiler.ast.TypedExpression;

import static org.drools.util.StringUtils.isEmpty;
import static org.drools.mvelcompiler.util.OptionalUtils.map2;

public class MvelCompilerContext {

    private final Map<String, Declaration> declarations = new HashMap<>();
    private final Map<String, StaticMethod> staticMethods = new HashMap<>();
    private final Map<String, DeclaredFunction> declaredFunctions = new HashMap<>();

    private final TypeResolver typeResolver;
    private final String scopeSuffix;
    private final Set<String> usedBindings = new HashSet<>();

    // Used in ConstraintParser
    private Optional<Class<?>> rootPattern = Optional.empty();
    private Optional<String> rootPrefix = Optional.empty();

    public MvelCompilerContext(TypeResolver typeResolver) {
        this(typeResolver, null);
    }

    public MvelCompilerContext(TypeResolver typeResolver, String scopeSuffix ) {
        this.typeResolver = typeResolver;
        this.scopeSuffix = isEmpty( scopeSuffix ) ? null : scopeSuffix;
    }

    public MvelCompilerContext addDeclaration(String name, Class<?> clazz) {
        declarations.put(name, new Declaration(name, clazz));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        Declaration d = declarations.get(name);
        if (d == null && scopeSuffix != null) {
            d = declarations.get( name + scopeSuffix );
        }
        return Optional.ofNullable(d);
    }

    public Optional<Class<?>> findEnum(String name) {
        try {
            return Optional.of(typeResolver.resolveType(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public Class<?> resolveType(String name) {
        try {
            return typeResolver.resolveType(name);
        } catch (ClassNotFoundException e) {
            throw new MvelCompilerException(e);
        }
    }

    public MvelCompilerContext addStaticMethod(String name, Method method) {
        staticMethods.put(name, new StaticMethod(name, method));
        return this;
    }

    public Optional<Method> findStaticMethod(String name) {
        return Optional.ofNullable(staticMethods.get(name)).map(StaticMethod::getMethod);
    }

    public MvelCompilerContext addDeclaredFunction(String name, String returnType, List<String> arguments) {
        declaredFunctions.put(name, new DeclaredFunction(this.typeResolver, name, returnType, arguments));
        return this;
    }

    public Optional<DeclaredFunction> findDeclaredFunction(String name) {
        return Optional.ofNullable(declaredFunctions.get(name));
    }

    public void setRootPatternPrefix(Class<?> rootPattern, String rootPrefix) {
        this.rootPattern = Optional.of(rootPattern);
        this.rootPrefix = Optional.of(rootPrefix);
    }

    public Optional<Class<?>> getRootPattern() {
        return rootPattern;
    }

    public Optional<TypedExpression> createRootTypePrefix() {
        return map2(rootPattern, rootPrefix, RootTypeThisExpr::new);
    }

    public void addUsedBinding(String s) {
        usedBindings.add(s);
    }

    public Set<String> getUsedBindings() {
        return usedBindings;
    }
}
