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

package org.drools.modelcompiler.builder.generator;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

/* Used to generate Consequence DSL */
class ConsequenceGenerator {

    private static String ARITY_CLASS_NAME = "_ARITY_CLASS_NAME";
    private static String ARITY_CLASS_BLOCK = "_ARITY_BLOCK";
    private static String ARITY_CLASS_BLOCK_PLUS_ONE = "_ARITY_BLOCK_PLUS_ONE";
    private static ClassOrInterfaceDeclaration templateInnerClass;
    private static CompilationUnit templateCU;
    private static ClassOrInterfaceDeclaration consequenceBuilder;
    private static int arity;

    public static void main(String[] args) throws Exception {
        arity = 12;

        try {
            templateCU = StaticJavaParser.parseResource("ConsequenceBuilder.java");
        } catch (IOException e) {
            throw e;
        }

        consequenceBuilder = templateCU.getClassByName("ConsequenceBuilder")
                .orElseThrow(() -> new RuntimeException("Main class not found"));

        templateInnerClass = consequenceBuilder
                .findAll(ClassOrInterfaceDeclaration.class, c -> ARITY_CLASS_NAME.equals(c.getNameAsString()))
                .stream()
                .findFirst()
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

        replaceName(arity, clone);
        replaceGenericType(arity, clone);

        consequenceBuilder.addMember(clone);
    }

    private static void replaceGenericType(int arity, ClassOrInterfaceDeclaration clone) {
        NodeList<TypeParameter> genericTypes = NodeList.nodeList();

        IntStream.range(1, arity + 1)
                .forEach(genericTypeIndex -> {
                    genericTypes.add(new TypeParameter("T" + genericTypeIndex));
                });

        clone.setTypeParameters(genericTypes);
    }

    private static void replaceName(int arity, ClassOrInterfaceDeclaration clone) {
        String arityName = "_" + arity;
        ClassOrInterfaceType arityType = parseClassOrInterfaceType(arityName);
        ClassOrInterfaceType arityBlockType = parseClassOrInterfaceType("Block" + arity);
        ClassOrInterfaceType arityBlockTypePlusOne = parseClassOrInterfaceType("Block" + (arity + 1));

        clone.findAll(ClassOrInterfaceDeclaration.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                .forEach(c -> c.setName(arityName));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                .forEach(oldType -> oldType.replace(arityType));

        clone.findAll(ConstructorDeclaration.class, findNodeWithNameArityClassName(ARITY_CLASS_NAME))
                .forEach(ctor -> ctor.setName(arityName));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_BLOCK))
                .forEach(oldType -> oldType.replace(arityBlockType));

        clone.findAll(ClassOrInterfaceType.class, findNodeWithNameArityClassName(ARITY_CLASS_BLOCK_PLUS_ONE))
                .forEach(oldType -> oldType.replace(arityBlockTypePlusOne));

    }

    static <N extends NodeWithSimpleName> Predicate<N> findNodeWithNameArityClassName(String name) {
        return c -> name.equals(c.getName().asString());
    }
}
