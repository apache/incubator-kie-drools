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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ApplicationContainerGenerator {

    public static final String APPLICATION_CLASS_NAME = "Application";
    private static final String RESOURCE_CDI = "/class-templates/CdiApplicationTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/SpringApplicationTemplate.java";
    private static final String RESOURCE_DEFAULT = "/class-templates/ApplicationTemplate.java";
    private final TemplatedGenerator templatedGenerator;

    private List<String> sections = new ArrayList<>();
    private KogitoBuildContext buildContext;

    public ApplicationContainerGenerator(KogitoBuildContext buildContext, String packageName) {
        this.templatedGenerator = new TemplatedGenerator(
                buildContext,
                packageName,
                APPLICATION_CLASS_NAME,
                RESOURCE_CDI,
                RESOURCE_SPRING,
                RESOURCE_DEFAULT);

        this.buildContext = buildContext;
    }

    public ApplicationContainerGenerator withSections(List<String> sections) {
        this.sections = sections;
        return this;
    }

    protected CompilationUnit getCompilationUnitOrThrow() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("Cannot find template for " + templatedGenerator.typeName());

        ClassOrInterfaceDeclaration cls = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        APPLICATION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "Compilation unit doesn't contain a class or interface declaration!"));

        // ApplicationTemplate (no CDI/Spring) has placeholders to replace
        if (buildContext == null || buildContext instanceof JavaKogitoBuildContext) {
            replacePlaceholder(getLoadEnginesMethod(cls), sections);
        }

        cls.getMembers().sort(new BodyDeclarationComparator());

        return compilationUnit;
    }

    public GeneratedFile generate() {
        return new GeneratedFile(GeneratedFile.Type.APPLICATION,
                templatedGenerator.generatedFilePath(),
                getCompilationUnitOrThrow().toString());
    }

    private MethodCallExpr getLoadEnginesMethod(ClassOrInterfaceDeclaration cls) {
        return cls.findFirst(MethodCallExpr.class, mtd -> "loadEngines".equals(mtd.getNameAsString()))
                    .orElseThrow(() -> new InvalidTemplateException(
                            APPLICATION_CLASS_NAME,
                            templatedGenerator.templatePath(),
                            "Impossible to find loadEngines invocation"));
    }

    private void replacePlaceholder(MethodCallExpr methodCallExpr, List<String> sections) {
        // look for expressions that contain $ and replace them with an initializer
        //      e.g.: $Processes$
        // is replaced with:
        //      e.g.: new Processes(this)
        // or remove the parameter if section is not available

        Map<String, Expression> replacementMap = sections.stream()
                .collect(toMap(
                        identity(),
                        sectionClassName -> new ObjectCreationExpr()
                                .setType(sectionClassName)
                                .addArgument(new ThisExpr())
                ));

        List<Expression> toBeRemoved = methodCallExpr.getArguments().stream()
                .filter(exp -> exp.toString().contains("$"))
                .filter(argument -> !replace(argument, replacementMap))
                .collect(toList());

        // remove using parentNode.remove(node) instead of node.remove() to prevent ConcurrentModificationException
        toBeRemoved.forEach(methodCallExpr::remove);
    }

    private boolean replace(Expression originalExpression, Map<String, Expression> expressionMap) {
        for (Map.Entry<String, Expression> entry : expressionMap.entrySet()) {
            if (originalExpression.toString().contains("$" + entry.getKey() + "$")) {
                originalExpression.replace(entry.getValue());
                return true;
            }
        }
        return false;
    }
}
