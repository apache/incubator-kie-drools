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

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parse;

public class JavaParserSourceGenerator {

    private ClassOrInterfaceDeclaration firstClass;
    private CompilationUnit compilationUnit;

    public static NodeList<Modifier> PUBLIC_STATIC_FINAL = NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier(), Modifier.finalModifier());

    public JavaParserSourceGenerator(String className, String namespace, String packageName) {
        this.compilationUnit = parse("public class " + className + namespace + "{ }");
        this.compilationUnit.setPackageDeclaration(packageName);
        firstClass = this.compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("Cannot find Class"));
    }

    public void addImports(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            compilationUnit.addImport(clazz);
        }
    }

    public void addStaticImportStar(Class<?> clazz) {
        compilationUnit.addImport(clazz.getName(), true, true);
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
        return StaticJavaParser.parseClassOrInterfaceType(canonicalName);
    }

    private ClassOrInterfaceType getType(Class<?> clazz) {
        return StaticJavaParser.parseClassOrInterfaceType(clazz.getCanonicalName());
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
