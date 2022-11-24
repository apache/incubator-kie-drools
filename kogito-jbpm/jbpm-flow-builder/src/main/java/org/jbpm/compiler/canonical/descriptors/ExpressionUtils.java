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
package org.jbpm.compiler.canonical.descriptors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.jbpm.process.core.datatype.impl.coverter.TypeConverterRegistry;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ExpressionUtils {

    private ExpressionUtils() {
    }

    public static void checkValid(String lang, String expr) {
        org.kie.kogito.process.expr.Expression exprObj = ExpressionHandlerFactory.get(lang, expr);
        if (!exprObj.isValid()) {
            throw new IllegalArgumentException(String.format("Expression %s for language %s is not a valid one", expr, lang), exprObj.validationError());
        }
    }

    public static ObjectCreationExpr getObjectCreationExpr(Class<?> runtimeClass, Object... args) {
        ObjectCreationExpr result = new ObjectCreationExpr().setType(runtimeClass.getCanonicalName());
        for (Object arg : args) {
            result.addArgument(getLiteralExpr(arg));
        }
        return result;
    }

    public static ObjectCreationExpr getObjectCreationExpr(ClassOrInterfaceType type, Object... args) {
        ObjectCreationExpr result = new ObjectCreationExpr().setType(type);
        for (Object arg : args) {
            result.addArgument(getLiteralExpr(arg));
        }
        return result;
    }

    public static MethodCallExpr getCollectionCreationExpr(Collection<?> collection) {
        if (collection.isEmpty()) {
            return getStaticMethodCall(Collections.class, "emptyList");
        } else {
            MethodCallExpr expr = getStaticMethodCall(Arrays.class, "asList");
            for (Object item : collection) {
                expr.addArgument(getLiteralExpr(item));
            }
            return expr;
        }
    }

    public static boolean isTypeSupported(Object object) {
        return object == null || object instanceof Supplier<?> || object instanceof Expression || object instanceof Boolean || object instanceof Character || object instanceof Number
                || object instanceof String || object instanceof Enum || object instanceof Collection || object instanceof Class<?> || isTypeRegistered(object.getClass());
    }

    public static Expression getLiteralExpr(Object object) {
        if (object == null) {
            return new NullLiteralExpr();
        } else if (object instanceof Supplier<?>) {
            return ((Supplier<Expression>) object).get();
        } else if (object instanceof Expression) {
            return (Expression) object;
        } else if (object instanceof Boolean) {
            return new BooleanLiteralExpr(((Boolean) object));
        } else if (object instanceof Character) {
            return new CharLiteralExpr(((Character) object));
        } else if (object instanceof Long) {
            return new LongLiteralExpr(object.toString());
        } else if (object instanceof Integer || object instanceof Short) {
            return new IntegerLiteralExpr(object.toString());
        } else if (object instanceof BigInteger) {
            return new BigIntegerLiteralExpr((BigInteger) object);
        } else if (object instanceof BigDecimal) {
            return new BigDecimalLiteralExpr((BigDecimal) object);
        } else if (object instanceof Number) {
            return new DoubleLiteralExpr(((Number) object).doubleValue());
        } else if (object instanceof String) {
            return new StringLiteralExpr().setString(object.toString());
        } else if (object instanceof Enum) {
            return new FieldAccessExpr(new NameExpr(object.getClass().getCanonicalName()), ((Enum<?>) object).name());
        } else if (object instanceof Collection) {
            return getCollectionCreationExpr((Collection<?>) object);
        } else if (object instanceof Class<?>) {
            return new ClassExpr(parseClassOrInterfaceType(((Class<?>) object).getCanonicalName()));
        } else {
            return convertExpression(object);
        }
    }

    public static MethodCallExpr getStaticMethodCall(Class<?> clazz, String methodName) {
        return new MethodCallExpr(clazz.getCanonicalName() + "." + methodName);
    }

    private static Expression convertExpression(Object object) {
        Class<?> objectClass = object.getClass();
        while (objectClass != null && !TypeConverterRegistry.get().isRegistered(objectClass.getName())) {
            objectClass = objectClass.getSuperclass();
        }
        if (objectClass != null) {
            // will generate TypeConverterRegistry.get().forType("JsonNode.class").apply("{\"dog\":\"perro\"}"))
            return new MethodCallExpr(new MethodCallExpr(new MethodCallExpr(new TypeExpr(StaticJavaParser.parseClassOrInterfaceType(TypeConverterRegistry.class.getName())), "get"), "forType",
                    NodeList.nodeList(new StringLiteralExpr(objectClass.getName()))), "apply",
                    NodeList.nodeList(new StringLiteralExpr().setString(TypeConverterRegistry.get().forTypeReverse(object).apply((object)))));
        } else {
            return new StringLiteralExpr().setString(object.toString());
        }
    }

    private static boolean isTypeRegistered(Class<?> objectClass) {
        while (objectClass != null && !TypeConverterRegistry.get().isRegistered(objectClass.getName())) {
            objectClass = objectClass.getSuperclass();
        }
        return objectClass != null;
    }
}
