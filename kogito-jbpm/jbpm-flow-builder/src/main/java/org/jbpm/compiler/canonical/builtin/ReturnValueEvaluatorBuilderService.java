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
package org.jbpm.compiler.canonical.builtin;

import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.process.core.ContextResolver;
import org.jbpm.util.JbpmClassLoaderUtil;

import com.github.javaparser.ast.expr.Expression;

public class ReturnValueEvaluatorBuilderService {

    private static ReturnValueEvaluatorBuilderService INSTANCE;
    private List<ReturnValueEvaluatorBuilder> builders;

    private ReturnValueEvaluatorBuilderService(ClassLoader contextClassLoader) {
        builders = ServiceLoader.load(ReturnValueEvaluatorBuilder.class, contextClassLoader)
                .stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }

    public static ReturnValueEvaluatorBuilderService instance() {
        if (INSTANCE == null) {
            INSTANCE = instance(JbpmClassLoaderUtil.findClassLoader());
        }
        return INSTANCE;
    }

    public static ReturnValueEvaluatorBuilderService instance(ClassLoader contextClassLoader) {
        return new ReturnValueEvaluatorBuilderService(contextClassLoader);
    }

    public ReturnValueEvaluatorBuilderService() {
        this(JbpmClassLoaderUtil.findClassLoader());
    }

    public Expression build(ContextResolver resolver, String dialect, String expression) {
        return build(resolver, dialect, expression, Object.class, null);
    }

    public Expression build(ContextResolver resolver, String dialect, String expression, Class<?> type, String root) {
        for (ReturnValueEvaluatorBuilder builder : builders) {
            if (builder.accept(dialect)) {
                return builder.build(resolver, expression, type, root);
            }
        }
        throw new IllegalArgumentException("No dialect found " + dialect + " for return expression evaluator building return value expression");
    }
}
