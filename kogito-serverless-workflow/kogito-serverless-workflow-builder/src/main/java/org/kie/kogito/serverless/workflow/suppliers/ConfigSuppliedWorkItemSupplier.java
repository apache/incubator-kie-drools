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
package org.kie.kogito.serverless.workflow.suppliers;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.kie.kogito.serverless.workflow.workitemparams.ConfigSuppliedWorkItemResolver;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ConfigSuppliedWorkItemSupplier<T> extends ConfigSuppliedWorkItemResolver<T> implements Supplier<Expression> {

    private final ObjectCreationExpr expression;

    public ConfigSuppliedWorkItemSupplier(String key, Class<T> clazz, T defaultValue, UnaryOperator<T> transformer, Expression transformerExpr) {
        super(key, clazz, defaultValue, transformer);
        this.expression =
                ExpressionUtils.getObjectCreationExpr(
                        parseClassOrInterfaceType(ConfigSuppliedWorkItemResolver.class.getCanonicalName()).setTypeArguments(parseClassOrInterfaceType(clazz.getCanonicalName())), key, clazz,
                        defaultValue, transformerExpr);
    }

    @Override
    public Expression get() {
        return expression;
    }
}
