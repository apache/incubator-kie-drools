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

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.ast.NodeList.nodeList;

/* Used to generate Consequence DSL */
class ConsequenceDSLGenerator {

    private static int arity;

    public static void main(String[] args) {
        arity = 24;

        for (int i = 1; i <= arity; i++) {
            generateMethod(i);
        }
    }

    private static void generateMethod(int arity) {

        String template = "    public static <A, B> ConsequenceBuilder._2<A, B> on(Variable<A> decl1, Variable<B> decl2) {\n" +
                "        return new ConsequenceBuilder._2(decl1, decl2);\n" +
                "    }";

        MethodDeclaration bodyDeclaration = (MethodDeclaration) StaticJavaParser.parseBodyDeclaration(template);

        ClassOrInterfaceType arityType = parseClassOrInterfaceType(arityName(arity));

        List<Parameter> parameters = genericTypeStream(arity, genericTypeIndex -> {
            ClassOrInterfaceType type = parseClassOrInterfaceType(String.format("Variable<%s>", argumentTypeName(genericTypeIndex)));
            return new Parameter(type, argName(genericTypeIndex));
        }).collect(Collectors.toList());
        bodyDeclaration.setParameters(nodeList(parameters));

        ClassOrInterfaceType arityTypeWithNameSpace = parseClassOrInterfaceType("ConsequenceBuilder." + arityName(arity));
        List<Expression> argumentCall = genericTypeStream(arity,
                                                          genericTypeIndex -> new NameExpr(argName(genericTypeIndex))).collect(Collectors.toList());
        bodyDeclaration.setParameters(nodeList(parameters));
        Expression expr = new ObjectCreationExpr(null, arityTypeWithNameSpace, nodeList(argumentCall));

        bodyDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(expr))));

        List<TypeParameter> genericTypeParameterList =
                genericTypeStream(arity, ConsequenceDSLGenerator::createTypeParameter)
                        .collect(Collectors.toList());

        List<Type> genericTypeList =
                genericTypeStream(arity, ConsequenceDSLGenerator::parseType)
                        .collect(Collectors.toList());

        ClassOrInterfaceType newType = parseClassOrInterfaceType("ConsequenceBuilder." + arityName(arity));
        newType.setTypeArguments(nodeList(genericTypeList));
        bodyDeclaration.setType(newType);

        arityType.setTypeArguments(nodeList(genericTypeList));

        bodyDeclaration.setTypeParameters(nodeList(genericTypeParameterList));

        System.out.println(bodyDeclaration.toString());
        System.out.println();
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

    private static String arityName(int arity) {
        return "_" + arity;
    }
}
