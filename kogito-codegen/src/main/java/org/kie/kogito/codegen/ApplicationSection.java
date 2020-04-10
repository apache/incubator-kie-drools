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

import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

/**
 * A descriptor for a "section" of the root Application class.
 * It contains a factory method for the section (which is an object instance)
 * and the corresponding class.
 *
 * This is to allow the pattern:
 *    app.$sectionname().$method()
 *
 * e.g.:
 *    app.processes().createMyProcess()
 */
public interface ApplicationSection {

    String sectionClassName();

    FieldDeclaration fieldDeclaration();

    MethodDeclaration factoryMethod();

    ClassOrInterfaceDeclaration classDeclaration();

    default CompilationUnit injectableClass() {
        return null;
    }

    default List<Statement> setupStatements() {
        return Collections.emptyList();
    }

}
