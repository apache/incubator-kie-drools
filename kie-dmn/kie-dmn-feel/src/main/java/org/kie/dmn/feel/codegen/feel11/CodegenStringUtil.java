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
package org.kie.dmn.feel.codegen.feel11;

import java.io.InputStream;

import javax.lang.model.SourceVersion;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CodegenStringUtil {

    /**
     * Escape for identifier part (not beginning)
     *
     * Similar to drools-model's StringUtil
     */
    public static String escapeIdentifier(String partOfIdentifier) {
        String id = partOfIdentifier;
        if (!Character.isJavaIdentifierStart(id.charAt(0))) {
            id = "_" + id;
        }
        id = id.replaceAll("_", "__");
        if (SourceVersion.isKeyword(id)) {
            id = "_" + id;
        }
        StringBuilder result = new StringBuilder();
        char[] cs = id.toCharArray();
        for (char c : cs) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
            } else {
                result.append("_" + Integer.valueOf(c));
            }
        }
        return result.toString();
    }

    public static void replaceSimpleNameWith(Node source, String oldName, String newName) {
        source.findAll(SimpleName.class, ne -> ne.toString().equals(oldName))
                .forEach(r -> r.replace(new SimpleName(newName)));
    }

    public static void replaceStringLiteralExprWith(Node source, String oldName, String newName) {
        source.findFirst(StringLiteralExpr.class, n -> n.getValue().equals(oldName))
                .ifPresent(s -> s.replace(new StringLiteralExpr(newName)));
    }

    public static void replaceIntegerLiteralExprWith(Node source, int oldValue, int newValue) {
        source.findFirst(IntegerLiteralExpr.class, n -> n.asInt() == oldValue).ifPresent(n -> n.replace(new IntegerLiteralExpr(newValue)));
    }

    public static boolean blockHasComment(BlockStmt block, String comment) {
        return block.getComment().filter(c -> comment.trim().equals(c.getContent().trim()))
                .isPresent();
    }

    public static CompilationUnit parseJavaClassTemplateFromResources(Class<?> resourceClass, String templateName) {
        InputStream resourceAsStream = resourceClass.getResourceAsStream(templateName);
        if(resourceAsStream == null) {
            throw new RuntimeException("Cannot find template: " + templateName);
        }
        CompilationUnit compilationUnit = StaticJavaParser.parse(resourceAsStream);
        compilationUnit.removeComment();
        return compilationUnit;
    }

    public static MethodDeclaration findMethodTemplate(ClassOrInterfaceDeclaration alphaNodeCreationClass, String methodName) {
        MethodDeclaration methodTemplate = alphaNodeCreationClass
                .findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals(methodName))
                .orElseThrow(() -> new RuntimeException("Cannot find method: " + methodName + " in class " + alphaNodeCreationClass));
        methodTemplate.remove();
        methodTemplate.setComment(null);
        return methodTemplate;
    }
}