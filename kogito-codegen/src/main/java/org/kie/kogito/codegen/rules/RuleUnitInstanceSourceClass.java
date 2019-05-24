/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.rules;

import java.lang.reflect.Method;

import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.impl.AbstractRuleUnitInstance;
import org.kie.kogito.rules.impl.ListDataSource;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class RuleUnitInstanceSourceClass {

    private final String packageName;
    private final String typeName;
    private final String canonicalName;
    private final String targetTypeName;
    private final String targetCanonicalName;
    private final String generatedFilePath;

    public static String qualifiedName(String packageName, String typeName) {
        return packageName + "." + typeName + "RuleUnitInstance";
    }

    public RuleUnitInstanceSourceClass(String packageName, String typeName) {
        this.packageName = packageName;
        this.typeName = typeName;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "RuleUnitInstance";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    private MethodDeclaration bindMethod() {
        // we are currently relying on reflection, but proper way to do this
        // would be to use JavaParser on the src class AND fallback
        // on reflection if the class is not available.
        Class<?> typeClass;
        try {
            typeClass = Thread.currentThread().getContextClassLoader().loadClass(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        MethodDeclaration methodDeclaration = new MethodDeclaration();

        BlockStmt methodBlock = new BlockStmt();
        methodDeclaration.setName("bind")
                .addModifier(Modifier.Keyword.PROTECTED)
                .addParameter(KieSession.class.getCanonicalName(), "rt")
                .addParameter(typeName, "value")
                .setType(void.class)
                .setBody(methodBlock);

        try {

            for (Method m : typeClass.getDeclaredMethods()) {
                m.setAccessible(true);
                if (m.getReturnType() == DataSource.class) {
                    EnclosedExpr casted = new EnclosedExpr(
                            //  ((ListDataSource) value.$method())
                            new CastExpr()
                                    .setType(ListDataSource.class.getCanonicalName())
                                    .setExpression(new MethodCallExpr(new NameExpr("value"), m.getName())));

                    // .drainInto(rt::insert)
                    MethodCallExpr drainInto = new MethodCallExpr(casted, "drainInto").addArgument(
                            new MethodReferenceExpr().setScope(new NameExpr("rt")).setIdentifier("insert"));

                    methodBlock.addStatement(drainInto);
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }

        return methodDeclaration;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration classDecl = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC);
        classDecl
                .addExtendedType(
                        new ClassOrInterfaceType(null, AbstractRuleUnitInstance.class.getCanonicalName())
                                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName)))
                .addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(RuleUnitSourceClass.ruleUnitType(canonicalName), "unit")
                .addParameter(canonicalName, "value")
                .addParameter(KieSession.class.getCanonicalName(), "session")
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr("unit"),
                        new NameExpr("value"),
                        new NameExpr("session")
                )));
        classDecl.addMember(bindMethod());
        return classDecl;
    }
}
