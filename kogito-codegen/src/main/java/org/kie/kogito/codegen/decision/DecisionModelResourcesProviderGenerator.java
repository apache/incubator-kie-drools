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

package org.kie.kogito.codegen.decision;

import java.util.List;
import java.util.NoSuchElementException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.api.management.GAV;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.decision.DecisionModelType;
import org.kie.kogito.dmn.DefaultDecisionModelResource;

import static org.kie.kogito.codegen.CodegenUtils.newObject;
import static org.kie.kogito.codegen.decision.ReadResourceUtil.getReadResourceMethod;

public class DecisionModelResourcesProviderGenerator {

    private final KogitoBuildContext context;
    private final String applicationCanonicalName;
    private final List<DMNResource> resources;
    private final TemplatedGenerator generator;

    public DecisionModelResourcesProviderGenerator(final KogitoBuildContext context,
                                                   final String applicationCanonicalName,
                                                   final List<DMNResource> resources) {
        this.context = context;
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = resources;
        this.generator = TemplatedGenerator.builder()
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .build(context, "DecisionModelResourcesProvider");
    }

    public String generate() {
        final CompilationUnit compilationUnit = generator.compilationUnitOrThrow();

        final ClassOrInterfaceDeclaration clazz = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withSingletonComponent(clazz);
        }

        if (context.getAddonsConfig().useTracing()) {
            setupResourcesVariable(clazz);
        }
        return compilationUnit.toString();
    }

    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    private void setupResourcesVariable(final ClassOrInterfaceDeclaration typeDeclaration) {
        final List<MethodDeclaration> getResourcesMethods = typeDeclaration.getMethodsBySignature("getResources");
        final ClassOrInterfaceType applicationClass = StaticJavaParser.parseClassOrInterfaceType(applicationCanonicalName);

        if (getResourcesMethods.size() != 1) {
            throw new InvalidTemplateException(
                    generator,
                    "A \"getResources()\" method was not found");
        }
        final MethodDeclaration getResourcesMethod = getResourcesMethods.get(0);
        final BlockStmt body = getResourcesMethod.getBody().orElseThrow(() -> new RuntimeException("Can't find the body of the \"get()\" method."));
        final VariableDeclarator resourcePathsVariable = getResourcesMethod.findFirst(VariableDeclarator.class).orElseThrow(() -> new RuntimeException("Can't find a variable declaration in the \"get()\" method."));

        for (DMNResource resource : resources) {
            final MethodCallExpr add = new MethodCallExpr(resourcePathsVariable.getNameAsExpression(), "add");
            final MethodCallExpr getResAsStream = getReadResourceMethod(applicationClass, resource.getCollectedResource());
            final MethodCallExpr isr = new MethodCallExpr("readResource").addArgument(getResAsStream);
            add.addArgument(newObject(DefaultDecisionModelResource.class,
                                      mockGAV(),
                                      new StringLiteralExpr(resource.getDmnModel().getNamespace()),
                                      new StringLiteralExpr(resource.getDmnModel().getName()),
                                      makeDMNType(),
                                      isr));
            body.addStatement(body.getStatements().size() - 1, add);
        }
    }

    private ObjectCreationExpr mockGAV() {
        //TODO See https://issues.redhat.com/browse/FAI-239
        return newObject(GAV.class,
                         new StringLiteralExpr("dummy"),
                         new StringLiteralExpr("dummy"),
                         new StringLiteralExpr("0.0"));
    }

    private FieldAccessExpr makeDMNType() {
        NameExpr clazz = new NameExpr(DecisionModelType.class.getName());
        return new FieldAccessExpr(clazz, "DMN");
    }
}
