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
import org.jbpm.workflow.core.Constraint;

import com.github.javaparser.ast.expr.Expression;

public class ConstraintEvaluatorBuilderService {

    private static ConstraintEvaluatorBuilderService INSTANCE;
    private List<ConstraintEvaluatorBuilder> builders;

    public static ConstraintEvaluatorBuilderService instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConstraintEvaluatorBuilderService();
        }
        return INSTANCE;
    }

    public ConstraintEvaluatorBuilderService() {
        builders = ServiceLoader.load(ConstraintEvaluatorBuilder.class, JbpmClassLoaderUtil.findClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }

    public Expression build(ContextResolver resolver, Constraint constraint) {
        for (ConstraintEvaluatorBuilder builder : builders) {
            if (builder.accept(constraint)) {
                return builder.build(resolver, constraint);
            }
        }
        throw new IllegalArgumentException("No dialect found " + constraint.getDialect() + " for building constraint");
    }
}
