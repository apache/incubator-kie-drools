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

package org.kie.kogito.codegen;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

/**
 * Base implementation for an {@link ApplicationSection}.
 *
 * It provides a skeleton for a "section" in the Application generated class.
 * Subclasses may extend this base class and decorate the provided
 * simple implementations of the interface methods with custom logic.
 */
public class AbstractApplicationSection implements ApplicationSection {

    private final String innerClassName;
    private final String methodName;
    private final String classType;

    public AbstractApplicationSection(String innerClassName, String methodName, Class<?> classType) {
        this.innerClassName = innerClassName;
        this.methodName = methodName;
        this.classType = classType.getCanonicalName();
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        return new ClassOrInterfaceDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setName(innerClassName)
                .addImplementedType(classType);
    }

    @Override
    public FieldDeclaration fieldDeclaration() {
        return new FieldDeclaration()
                .addVariable(
                        new VariableDeclarator()
                                .setType(innerClassName)
                                .setName(methodName)
                                .setInitializer(new ObjectCreationExpr().setType(innerClassName)));
    }

    public MethodDeclaration factoryMethod() {
        return new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(innerClassName)
                .setName(methodName)
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new NameExpr(methodName))));
    }
}
