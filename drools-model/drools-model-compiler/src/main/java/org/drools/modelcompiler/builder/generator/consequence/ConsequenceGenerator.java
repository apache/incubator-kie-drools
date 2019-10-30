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

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.ast.NodeList.nodeList;

/* Used to generate ConsequenceBuilder File */
class ConsequenceGenerator {

    private static String ARITY_CLASS_NAME = "_ARITY_CLASS_NAME";
    private static String ARITY_CLASS_BLOCK = "_ARITY_BLOCK";
    private static String ARITY_CLASS_BLOCK_PLUS_ONE = "_ARITY_BLOCK_PLUS_ONE";
    private static ClassOrInterfaceDeclaration templateInnerClass;
    private static CompilationUnit templateCU;
    private static ClassOrInterfaceDeclaration consequenceBuilder;
    private static int arity;

    public static void main(String[] args) throws Exception {
        arity = 24;

        templateCU = StaticJavaParser.parseResource("ConsequenceBuilderTemplate.java");

        consequenceBuilder = templateCU.getClassByName("ConsequenceBuilder")
                .orElseThrow(() -> new RuntimeException("Main class not found"));

        templateInnerClass = consequenceBuilder
                .findFirst(ClassOrInterfaceDeclaration.class, c -> ARITY_CLASS_NAME.equals(c.getNameAsString()))
                .orElseThrow(() -> new RuntimeException("Inner class not found"));

        consequenceBuilder.remove(templateInnerClass);

        for (int i = 1; i <= arity; i++) {
            generateInnerClass(i);
        }

        System.out.println(templateCU);
    }

    private static void generateInnerClass(int arity) {
        ClassOrInterfaceDeclaration clone = templateInnerClass.clone();
        clone.setComment(null);

        ConstructorDeclaration constructor = findConstructor(clone);

        replaceName(arity, clone, constructor);
        replaceGenericType(arity, clone, constructor);

        consequenceBuilder.addMember(clone);
    }

    private static ConstructorDeclaration findConstructor(ClassOrInterfaceDeclaration clone) {
        return clone.findFirst(ConstructorDeclaration.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                    .orElseThrow(() -> new RuntimeException("Constructor not found"));
    }

    private static void replaceName(int arity, ClassOrInterfaceDeclaration clone, ConstructorDeclaration constructor) {
        ClassOrInterfaceType arityType = parseClassOrInterfaceType(arityName(arity));

        clone.findAll(ClassOrInterfaceDeclaration.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                .forEach(c -> c.setName(arityName(arity)));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                .forEach(oldType -> oldType.replace(arityType));

        constructor.setName(arityName(arity));

    }

    private static String arityName(int arity) {
        return "_" + arity;
    }

    private static <N extends NodeWithSimpleName> Predicate<N> findNodeWithNameArityClassName(String name) {
        return c -> name.equals(c.getName().asString());
    }

    private static void replaceGenericType(int arity, ClassOrInterfaceDeclaration clone, ConstructorDeclaration constructor) {
        List<TypeParameter> genericTypeParameterList =
                genericTypeStream(arity, ConsequenceGenerator::createTypeParameter)
                        .collect(Collectors.toList());
        clone.setTypeParameters(nodeList(genericTypeParameterList));

        List<Type> genericTypeList =
                genericTypeStream(arity, ConsequenceGenerator::parseType)
                        .collect(Collectors.toList());

        ClassOrInterfaceType extendTypeParameter = parseClassOrInterfaceType(arityName(arity));
        extendTypeParameter.setTypeArguments(nodeList(genericTypeList));

        ClassOrInterfaceType extendedType = new ClassOrInterfaceType(null, new SimpleName("AbstractValidBuilder"), nodeList(extendTypeParameter));

        clone.findAll(MethodDeclaration.class, mc -> mc.getType().asString().equals(arityName(arity)))
                .forEach(c -> c.setType(extendTypeParameter));


        clone.setExtendedTypes(nodeList(extendedType));

        List<Parameter> parameters = genericTypeStream(arity, genericTypeIndex -> {
            ClassOrInterfaceType type = parseClassOrInterfaceType(String.format("Variable<%s>", argumentTypeName(genericTypeIndex)));
            return new Parameter(type, argName(genericTypeIndex));
        }).collect(Collectors.toList());

        constructor.setParameters(nodeList(parameters));
        constructorBody(arity, constructor);

        ClassOrInterfaceType arityBlockType = parseClassOrInterfaceType("Block" + arity);
        arityBlockType.setTypeArguments(nodeList(genericTypeList));

        ClassOrInterfaceType arityBlockTypePlusOne = parseClassOrInterfaceType("Block" + (arity + 1));
        List<Type> genericTypeListPlusDrools = new ArrayList<>(genericTypeList);
        genericTypeListPlusDrools.add(0, parseClassOrInterfaceType("Drools"));
        arityBlockTypePlusOne.setTypeArguments(nodeList(genericTypeListPlusDrools));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_BLOCK))
                .forEach(oldType -> oldType.replace(arityBlockType));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_BLOCK_PLUS_ONE))
                .forEach(oldType -> oldType.replace(arityBlockTypePlusOne));

    }

    private static void constructorBody(int arity, ConstructorDeclaration constructor) {
        List<Expression> constructorArgument = genericTypeStream(arity,
                                                      genericTypeIndex -> new NameExpr(argName(genericTypeIndex))).collect(Collectors.toList());

        MethodCallExpr superCall = new MethodCallExpr(null, "super", nodeList(constructorArgument));
        constructor.setBody(new BlockStmt(nodeList(new ExpressionStmt(superCall))));
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
