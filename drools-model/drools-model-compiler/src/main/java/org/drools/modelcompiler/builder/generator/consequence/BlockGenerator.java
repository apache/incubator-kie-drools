/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.modelcompiler.builder.generator.consequence;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

/* Used to generate Block DSL */
class BlockGenerator {

    private static ClassOrInterfaceDeclaration templateInnerClass;
    private static CompilationUnit templateCU;
    private static int arity;
    private static CompilationUnit cloneCU;

    public static void main(String[] args) throws Exception {
        arity = 25;

        templateCU = StaticJavaParser.parseResource("BlockTemplate.java");

        for (int i = 1; i <= arity; i++) {
            generateClass(i);
        }
    }

    private static void generateClass(int arity) throws IOException {
        cloneCU = templateCU.clone();

        ClassOrInterfaceDeclaration blockClass = cloneCU.getInterfaceByName("BlockTemplate")
                .orElseThrow(() -> new RuntimeException("Main class not found"));

        blockClass.setName(arityName(arity));

        templateInnerClass = blockClass
                .findFirst(ClassOrInterfaceDeclaration.class, c -> "Impl".equals(c.getNameAsString()))
                .orElseThrow(() -> new RuntimeException("Inner class not found"));

        cloneCU.findAll(Type.class, t -> "BlockTemplate".equals(t.asString()))
                .forEach(t -> t.replace(parseClassOrInterfaceType(arityName(arity))));

        replaceGenericType(arity, blockClass, templateInnerClass);

        Path newFilePath = Paths.get(String.format("/tmp/block-classes/Block%d.java", arity));
        Path parent = newFilePath.getParent();
        try {
            Files.createDirectories(parent);
        } catch (FileAlreadyExistsException e) {

        }
        Files.write(newFilePath, cloneCU.toString().getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String arityName(int arity) {
        return "Block" + arity;
    }

    private static void replaceGenericType(int arity, ClassOrInterfaceDeclaration containerClass, ClassOrInterfaceDeclaration innerClass) {
        List<TypeParameter> genericTypeParameterList =
                genericTypeStream(arity, BlockGenerator::createTypeParameter)
                        .collect(Collectors.toList());
        containerClass.setTypeParameters(NodeList.nodeList(genericTypeParameterList));

        MethodDeclaration executeMethod = containerClass.findFirst(MethodDeclaration.class, mc -> "execute".equals(mc.getNameAsString()))
                .orElseThrow(() -> new RuntimeException("Execute method not found"));

        List<Parameter> params = genericTypeStream(arity, i -> {
            Type t = parseType(i);
            return new Parameter(t, argName(i));
        }).collect(Collectors.toList());
        executeMethod.setParameters(NodeList.nodeList(params));

        List<Expression> executParams = genericTypeStream(arity, i -> {
            NameExpr nameExpr = new NameExpr("objs");
            return new ArrayAccessExpr(nameExpr, new IntegerLiteralExpr(i - 1));
        }).collect(Collectors.toList());

        innerClass.findFirst(MethodDeclaration.class, mc -> "execute".equals(mc.getNameAsString()))
                .ifPresent(m -> {
                    BlockStmt blockStmt = new BlockStmt();
                    NodeList<Expression> expressions = NodeList.nodeList(executParams);
                    MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("block"), "execute", expressions);
                    blockStmt.setStatements(NodeList.nodeList(new ExpressionStmt(methodCallExpr)));
                    m.setBody(blockStmt);
                });

    }

    private static String argName(int genericTypeIndex) {
        return "arg" + genericTypeIndex;
    }

    private static <T> Stream<T> genericTypeStream(int arity, IntFunction<T> parseType) {
        return IntStream.range(1, arity + 1)
                .mapToObj(parseType);
    }

    private static ClassOrInterfaceType parseType(int genericTypeIndex) {
        return parseClassOrInterfaceType(argumentTypeName(genericTypeIndex));
    }

    private static String argumentTypeName(int genericTypeIndex) {
        return "T" + genericTypeIndex;
    }

    private static TypeParameter createTypeParameter(int genericTypeIndex) {
        return new TypeParameter(argumentTypeName(genericTypeIndex));
    }
}
