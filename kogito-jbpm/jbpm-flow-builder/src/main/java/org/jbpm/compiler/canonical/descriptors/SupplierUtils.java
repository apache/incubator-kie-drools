/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical.descriptors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class SupplierUtils {

    private SupplierUtils() {
    }

    public static ObjectCreationExpr getExpression(Class<?> runtimeClass, String... args) {
        ObjectCreationExpr result = new ObjectCreationExpr().setType(runtimeClass.getCanonicalName());
        for (String arg : args) {
            result.addArgument(arg == null ? new NullLiteralExpr() : new StringLiteralExpr().setString(arg));
        }
        return result;
    }

    public static MethodCallExpr getCollectionCreationExpr(Collection<?> collection) {
        if (collection.isEmpty()) {
            return getStaticMethodCall(Collections.class, "emptyList");
        } else {
            MethodCallExpr expr = getStaticMethodCall(Arrays.class, "asList");
            for (Object item : collection) {
                expr.addArgument(AbstractServiceTaskDescriptor.getLiteralExpr(item));
            }
            return expr;
        }
    }

    public static MethodCallExpr getStaticMethodCall(Class<?> clazz, String methodName) {
        return new MethodCallExpr(clazz.getCanonicalName() + "." + methodName);
    }

}
