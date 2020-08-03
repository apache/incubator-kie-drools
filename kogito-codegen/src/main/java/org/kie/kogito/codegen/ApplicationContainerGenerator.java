/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class ApplicationContainerGenerator extends TemplatedGenerator {

    public static final String APPLICATION_CLASS_NAME = "Application";
    private static final String RESOURCE_CDI = "/class-templates/CdiApplicationTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/SpringApplicationTemplate.java";
    private static final String RESOURCE_DEFAULT = "/class-templates/ApplicationTemplate.java";

    private List<String> sections = new ArrayList<>();

    public ApplicationContainerGenerator(String packageName) {
        super(packageName,
              APPLICATION_CLASS_NAME,
              RESOURCE_CDI,
              RESOURCE_SPRING,
              RESOURCE_DEFAULT);
    }

    public ApplicationContainerGenerator withSections(List<String> sections) {
        this.sections = sections;
        return this;
    }

    public CompilationUnit getCompilationUnitOrThrow() {
        return compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(
                        APPLICATION_CLASS_NAME,
                        templatePath(),
                        "Cannot find template for " + super.typeName()));
    }

    public Optional<CompilationUnit> compilationUnit() {
        Optional<CompilationUnit> optionalCompilationUnit = super.compilationUnit();
        CompilationUnit compilationUnit =
                optionalCompilationUnit
                        .orElseThrow(() -> new InvalidTemplateException(
                                APPLICATION_CLASS_NAME,
                                templatePath(),
                                "Cannot find template for " + super.typeName()));

        ClassOrInterfaceDeclaration cls = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        APPLICATION_CLASS_NAME,
                        templatePath(),
                        "Compilation unit doesn't contain a class or interface declaration!"));

        for (String section : sections) {
            replaceSectionPlaceHolder(cls, section);
        }

        cls.getMembers().sort(new BodyDeclarationComparator());
        return optionalCompilationUnit;
    }

    private void replaceSectionPlaceHolder(ClassOrInterfaceDeclaration cls, String sectionClassName) {
        // look for an expression of the form: foo = ... /* $SectionName$ */ ;
        //      e.g.: this.processes = null /* $Processes$ */;
        // and replaces the entire expression with an initializer; e.g.:
        //      e.g.: this.processes = new Processes(this);

        // new $SectionName$(this)
        ObjectCreationExpr sectionCreationExpr = new ObjectCreationExpr()
                .setType(sectionClassName)
                .addArgument(new ThisExpr());

        cls.findFirst(
                BlockComment.class, c -> c.getContent().trim().equals('$' + sectionClassName + '$'))
                .flatMap(Node::getParentNode)
                .map(ExpressionStmt.class::cast)
                .map(e -> e.getExpression().asAssignExpr())
                .ifPresent(assignExpr -> assignExpr.setValue(sectionCreationExpr));
        // else ignore: there is no such templated argument

    }
}
