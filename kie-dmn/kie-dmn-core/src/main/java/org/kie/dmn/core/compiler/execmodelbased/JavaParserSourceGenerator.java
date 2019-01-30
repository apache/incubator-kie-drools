/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.List;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.ArrayCreationLevel;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.ArrayCreationExpr;
import org.drools.javaparser.ast.expr.ArrayInitializerExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.type.ArrayType;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;

public class JavaParserSourceGenerator {

    private ClassOrInterfaceDeclaration firstClass;
    private CompilationUnit compilationUnit;

    public static NodeList<Modifier> PUBLIC_STATIC_FINAL = NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier(), Modifier.finalModifier());

    public JavaParserSourceGenerator(String className, String namespace, String packageName) {
        this.compilationUnit = JavaParser.parse("public class " + className + namespace + "{ }");
        this.compilationUnit.setPackageDeclaration(packageName);
        firstClass = this.compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("Cannot find Class"));
    }

    public void addImports(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            compilationUnit.addImport(clazz);
        }
    }

    public void addInnerClassWithName(ClassOrInterfaceDeclaration feelExpressionSource, String name) {
        renameFeelExpressionClass(name, feelExpressionSource);
        firstClass.addMember(feelExpressionSource);
    }

    public void addMember(FieldDeclaration feelExpressionSource) {
        firstClass.addMember(feelExpressionSource);
    }

    public void addField(String testClass, Class<?> type, String instanceName) {
        ClassOrInterfaceType innerClassType = getType(testClass);
        ObjectCreationExpr newInstanceOfInnerClass = new ObjectCreationExpr(null, innerClassType, NodeList.nodeList());
        VariableDeclarator variableDeclarator = new VariableDeclarator(getType(type), instanceName, newInstanceOfInnerClass);
        firstClass.addMember(new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator));
    }

    public void addTwoDimensionalArray(List<List<String>> initializers, String arrayName, Class<?> type) {
        NodeList<ArrayCreationLevel> arrayCreationLevels = NodeList.nodeList(new ArrayCreationLevel(), new ArrayCreationLevel());

        NodeList<Expression> arrayInitializers = NodeList.nodeList();
        for(List<String> innerInitializer : initializers) {
            NodeList<Expression> arrayInitializerInner = NodeList.nodeList();
            for(String instanceName : innerInitializer) {
                arrayInitializerInner.add(new NameExpr(instanceName));
            }
            arrayInitializers.add(new ArrayInitializerExpr(arrayInitializerInner));
        }

        ArrayInitializerExpr initializerMainArray = new ArrayInitializerExpr(arrayInitializers);
        ArrayCreationExpr arrayCreationExpr = new ArrayCreationExpr(getType(type), arrayCreationLevels, initializerMainArray);
        VariableDeclarator variable = new VariableDeclarator(new ArrayType(new ArrayType(getType(type))), arrayName, arrayCreationExpr);
        addMember(new FieldDeclaration(PUBLIC_STATIC_FINAL, variable));
    }


    private ClassOrInterfaceType getType(String canonicalName) {
        return JavaParser.parseClassOrInterfaceType(canonicalName);
    }

    private ClassOrInterfaceType getType(Class<?> clazz) {
        return JavaParser.parseClassOrInterfaceType(clazz.getCanonicalName());
    }

    private void renameFeelExpressionClass(String testClass, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final String finalTestClass = testClass;
        classOrInterfaceDeclaration
                .setName(finalTestClass);

        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(n -> n.replace(new ConstructorDeclaration(finalTestClass)));
    }

    public String getSource() {
        return compilationUnit.toString();
    }
}
