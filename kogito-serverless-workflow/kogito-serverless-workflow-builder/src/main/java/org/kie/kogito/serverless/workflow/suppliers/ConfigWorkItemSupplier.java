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

import org.jbpm.compiler.canonical.descriptors.AbstractServiceTaskDescriptor;
import org.kie.kogito.serverless.workflow.workitemparams.ConfigWorkItemResolver;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ConfigWorkItemSupplier<T> extends ConfigWorkItemResolver<T> implements Supplier<Expression> {

    private final ObjectCreationExpr expression;

    public ConfigWorkItemSupplier(String key, Class<T> clazz, T defaultValue) {
        super(key, clazz, defaultValue);
        this.expression = createExpression(ConfigWorkItemResolver.class, key, clazz, defaultValue);
    }

    @Override
    public Expression get() {
        return expression;
    }

    protected static final ObjectCreationExpr createExpression(Class<? extends ConfigWorkItemResolver> objectClass, String key, Class<?> clazz, Object defaultValue) {
        return new ObjectCreationExpr().setType(parseClassOrInterfaceType(objectClass.getCanonicalName()).setTypeArguments(StaticJavaParser.parseClassOrInterfaceType(clazz.getCanonicalName())))
                .addArgument(new StringLiteralExpr(key))
                .addArgument(new ClassExpr(parseClassOrInterfaceType(clazz.getCanonicalName()))).addArgument(
                        AbstractServiceTaskDescriptor.getLiteralExpr(defaultValue));
    }
}
