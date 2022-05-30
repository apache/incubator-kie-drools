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
package org.kie.kogito.codegen.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class CodegenUtils {

    private CodegenUtils() {
        // utility class
    }

    public static ClassOrInterfaceType genericType(Class<?> outer, Class<?> inner) {
        return genericType(outer.getCanonicalName(), inner.getCanonicalName());
    }

    public static ClassOrInterfaceType genericType(String outer, Class<?> inner) {
        return genericType(outer, inner.getCanonicalName());
    }

    public static ClassOrInterfaceType genericType(String outer, String inner) {
        return new ClassOrInterfaceType(null, outer).setTypeArguments(new ClassOrInterfaceType(null, inner));
    }

    public static MethodDeclaration method(Modifier.Keyword modifier, Class<?> type, String name, BlockStmt body) {
        return method(modifier, type, name, NodeList.nodeList(), body);
    }

    public static MethodDeclaration method(Modifier.Keyword modifier, Class<?> type, String name, NodeList<Parameter> parameters, BlockStmt body) {
        return new MethodDeclaration()
                .setModifiers(modifier)
                .setType(type == null ? "void" : type.getCanonicalName())
                .setName(name)
                .setParameters(parameters)
                .setBody(body);
    }

    public static ObjectCreationExpr newObject(Class<?> type) {
        return newObject(type.getCanonicalName());
    }

    public static ObjectCreationExpr newObject(Class<?> type, Expression... arguments) {
        return newObject(type.getCanonicalName(), arguments);
    }

    public static ObjectCreationExpr newObject(String type) {
        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, type), new NodeList<>());
    }

    public static ObjectCreationExpr newObject(String type, Expression... arguments) {
        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, type), NodeList.nodeList(arguments));
    }

    public static void interpolateArguments(NodeWithArguments<? extends Node> node, Map<String, Expression> expressions) {
        List<Expression> argumentList = node.getArguments();
        Map<String, Integer> argumentIds = new HashMap<>();

        expressions.forEach((placeholder, expr) -> {
            for (int i = 0; i < argumentList.size(); i++) {
                if (argumentList.get(i).toString().trim().equals(placeholder)) {
                    argumentIds.put(placeholder, i);
                }
            }
        });

        argumentIds.forEach((placeholder, id) -> {
            node.setArgument(id, expressions.get(placeholder));
        });
    }

    //Defaults the "to be interpolated type" to $Type$.
    public static void interpolateTypes(ClassOrInterfaceType t, String dataClazzName) {
        SimpleName returnType = t.getName();
        Map<String, String> interpolatedTypes = new HashMap<>();
        interpolatedTypes.put("$Type$", dataClazzName);
        interpolateTypes(returnType, interpolatedTypes);
        t.getTypeArguments().ifPresent(ta -> interpolateTypeArguments(ta, interpolatedTypes));
    }

    public static void interpolateTypes(ClassOrInterfaceType t, Map<String, String> typeInterpolations) {
        SimpleName returnType = t.getName();
        interpolateTypes(returnType, typeInterpolations);
        t.getTypeArguments().ifPresent(ta -> interpolateTypeArguments(ta, typeInterpolations));
    }

    public static void interpolateTypes(SimpleName returnType, Map<String, String> typeInterpolations) {
        typeInterpolations.entrySet().forEach(entry -> {
            String identifier = returnType.getIdentifier();
            String newIdentifier = identifier.replace(entry.getKey(), entry.getValue());
            returnType.setIdentifier(newIdentifier);
        });
    }

    public static void interpolateTypeArguments(NodeList<Type> ta, Map<String, String> typeInterpolations) {
        ta.stream().filter(Type::isClassOrInterfaceType).map(Type::asClassOrInterfaceType)
                .forEach(t -> interpolateTypes(t, typeInterpolations));
    }

    public static boolean isProcessField(FieldDeclaration fd) {
        return fd.getElementType().isClassOrInterfaceType() && fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Process");
    }

    public static boolean isApplicationField(FieldDeclaration fd) {
        return fd.getElementType().isClassOrInterfaceType() && fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Application");
    }

    public static boolean isConfigBeanField(FieldDeclaration fd) {
        return fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("ConfigBean");
    }

    public static boolean isObjectMapperField(FieldDeclaration fd) {
        return fd.getElementType().isClassOrInterfaceType() && fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("ObjectMapper");
    }
}
